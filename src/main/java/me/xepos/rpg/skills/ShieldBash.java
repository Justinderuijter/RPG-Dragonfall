package me.xepos.rpg.skills;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.ApplyStunTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import java.util.UUID;

public class ShieldBash extends XRPGActiveSkill {

    public ShieldBash(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        AttributeModifier mod = new AttributeModifier(UUID.fromString("076c8ed9-b6e2-4da1-a4c0-27c50c61725d"), "SHIELD_BASH", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

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
        if (!isSkillReady()){
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        RayTraceResult result = Utils.rayTrace(player, 8, FluidCollisionMode.NEVER);

        if (result != null && result.getHitEntity() != null){
            final double castDelay = getSkillVariables().getDouble("cast-delay", 0.25);
            final double duration = getSkillVariables().getDouble("duration", 1.0);

            if (result.getHitEntity() instanceof Player){
                XRPGPlayer xrpgPlayer = getPlugin().getXRPGPlayer(result.getHitEntity().getUniqueId(), true);
                if (xrpgPlayer != null && xrpgPlayer.canBeStunned() && !canHurtTarget((Player) result.getHitEntity()))
                    new ApplyStunTask(xrpgPlayer, AttributeModifierManager.getInstance().get(ModifierType.NEGATIVE, "SHIELD_BASH").getAttributeModifier(), (long) duration * 20, getPlugin()).runTaskLater(getPlugin(), (long) castDelay * 20);
                else
                    player.sendMessage(ChatColor.RED + result.getHitEntity().getName() + " cannot be stunned for " + xrpgPlayer.getStunblockDuration() + " seconds!");
            }else {
                ((LivingEntity)result.getHitEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 6, false, false, false));
            }

            setRemainingCooldown(getCooldown());
        }

/*        if (e.getEntity() instanceof Player && player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
            Player target = (Player) e.getEntity();
            //Check if the location is valid and player isn't allied before casting shield Bash
            if (getProtectionSet().isLocationValid(player.getLocation(), target.getLocation()) && !getPartyManager().isPlayerAllied(player, target)) {
                if (!isSkillReady()) {
                    player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                    return;
                }



                setRemainingCooldown(getCooldown());
            }
        }*/
    }
}
