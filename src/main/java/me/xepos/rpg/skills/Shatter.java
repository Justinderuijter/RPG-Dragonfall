package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.events.XRPGDamageTakenAddedEvent;
import me.xepos.rpg.skills.base.FireballStackData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Shatter extends XRPGActiveSkill {
    private FireballStackData fireballStackData;

    public Shatter(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, FireballStackData fireballStackData) {
        super(xrpgPlayer, skillVariables, plugin);

        this.fireballStackData = fireballStackData;
        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    public Shatter(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doShatter(e);
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getPassiveEventHandler("RIGHT_CLICK").getSkills().values()) {
            if (skill instanceof Fireball) {
                this.fireballStackData = ((Fireball) skill).getFireballStackData();
                return;
            }
        }
    }

    private void doShatter(PlayerItemHeldEvent e) {
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        Location loc = Utils.getTargetBlock(e.getPlayer(), 32).getLocation();
        List<LivingEntity> livingEntities = new ArrayList(loc.getWorld().getNearbyEntities(loc, 3, 3, 3, p -> p instanceof LivingEntity && p != e.getPlayer()));

        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1F, 1F);
        for (LivingEntity livingEntity : livingEntities) {
            shatterLogic(e, livingEntity);
        }
        int fireBallStacks = 0;
        if (fireballStackData != null) {
            fireBallStacks = fireballStackData.getFireBallStacks();
        }

        setRemainingCooldown(getCooldown() - fireBallStacks);
    }

    public void shatterLogic(PlayerItemHeldEvent e, LivingEntity livingEntity) {

        final double duration = getSkillVariables().getDouble("duration", 4);
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, (int) (duration * 20), 1, false, false, false);

        if (livingEntity instanceof Player) {
            Player targetPlayer = (Player) livingEntity;
            //Check if the target is valid
            if (getProtectionSet().isLocationValid(e.getPlayer().getLocation(), targetPlayer.getLocation())) {

                final double shatterDTAmount = getSkillVariables().getDouble("dt-amount", 1.2);
                final double shatterDTDuration = getSkillVariables().getDouble("dt-duration", 4.0);
                //Add potion effect and fire event
                targetPlayer.addPotionEffect(potionEffect);
                targetPlayer.damage(getDamage(), e.getPlayer());
                XRPGDamageTakenAddedEvent event = new XRPGDamageTakenAddedEvent(e.getPlayer(), targetPlayer, this, shatterDTAmount);
                Bukkit.getServer().getPluginManager().callEvent(event);

                //Apply DTModifier if the event isn't cancelled
                if (!event.isCancelled()) {
                    XRPGPlayer xrpgTarget = getPlugin().getXRPGPlayer(targetPlayer);
                    Utils.addDTModifier(xrpgTarget, getSkillName(), shatterDTAmount);
                    new RemoveDTModifierTask(e.getPlayer(), xrpgTarget, this).runTaskLater(getPlugin(), (long) shatterDTDuration * 20L);
                }
            }
        } else {
            livingEntity.addPotionEffect(potionEffect);
        }

        setRemainingCooldown(getCooldown());
    }
}
