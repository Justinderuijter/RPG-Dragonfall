package me.xepos.rpg.skills;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.ApplyStunTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ShieldBash extends XRPGActiveSkill {

    private static final String stunAttributeName = XRPG.modifierPrefix + "SHIELD_BASH_";
    public ShieldBash(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        AttributeModifier mod = new AttributeModifier(UUID.fromString("076c8ed9-b6e2-4da1-a4c0-27c50c61725d"), stunAttributeName + skillLevel, -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        AttributeModifierManager.getInstance().put(ModifierType.NEGATIVE, mod.getName(), mod, Attribute.GENERIC_MOVEMENT_SPEED);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doShieldBash(e, e.getPlayer());
    }

    @Override
    public void initialize() {

    }

    private void doShieldBash(PlayerItemHeldEvent e, Player player) {
        if  (player.getInventory().getItemInOffHand().getType() != Material.SHIELD){
            player.sendMessage( ChatColor.RED + "You must have a shield equipped in your off hand to cast this ability!");
            return;
        }else if (!isSkillReady()){
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        RayTraceResult result = Utils.rayTrace(player, 4, FluidCollisionMode.NEVER);

        if (result != null && result.getHitEntity() != null){
            final double castDelay = getSkillVariables().getDouble(getSkillLevel(), "cast-delay", 0.25);
            final double duration = getSkillVariables().getDouble(getSkillLevel(), "duration", 1.0);
            final double pushStrength = getSkillVariables().getDouble(getSkillLevel(), "push-strength", 1.5);

            if (result.getHitEntity() instanceof Player){
                XRPGPlayer xrpgPlayer = getPlugin().getPlayerManager().getXRPGPlayer(result.getHitEntity().getUniqueId(), true);
                if (xrpgPlayer != null && xrpgPlayer.canBeStunned() && !canHurtTarget((Player) result.getHitEntity()))
                    new ApplyStunTask(xrpgPlayer, AttributeModifierManager.getInstance().get(ModifierType.NEGATIVE, stunAttributeName + getSkillLevel()).getAttributeModifier(), (long) duration * 20, getPlugin()).runTaskLater(getPlugin(), (long) castDelay * 20);
                else
                    player.sendMessage(ChatColor.RED + result.getHitEntity().getName() + " cannot be stunned for " + xrpgPlayer.getStunblockDuration() + " seconds!");
            }else {
                ((LivingEntity)result.getHitEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 6, false, false, false));
            }

            LivingEntity target = (LivingEntity) result.getHitEntity();
            Vector unitVector = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

            target.setVelocity(unitVector.multiply(pushStrength));
            target.damage(getDamage(), player);

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }
}
