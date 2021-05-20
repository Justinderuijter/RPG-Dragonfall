package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class Backstab extends XRPGPassiveSkill {
    public Backstab(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getPassiveEventHandler("SNEAK").addSkill(this.getClass().getSimpleName(), this);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerToggleSneakEvent){
            PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
            if (!e.getPlayer().isSneaking()){
                if (!isSkillReady()){
                    e.getPlayer().sendMessage(getCooldownMessage());
                    return;
                }
                e.getPlayer().setInvisible(true);
            }else{
                e.getPlayer().setInvisible(false);
            }

        } else if(event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            LivingEntity damager;
            if (e.getDamager() instanceof Projectile) {
                damager = (LivingEntity) ((Projectile) e.getDamager()).getShooter();
            } else {
                damager = (LivingEntity) e.getDamager();
            }

            if (damager.isInvisible()) {
                Vector attackerDirection = damager.getLocation().getDirection();
                Vector victimDirection = e.getEntity().getLocation().getDirection();
                //determine if the dot product between the vectors is greater than 0
                //If it is, we can conclude that the attack was a backstab
                if (attackerDirection.dot(victimDirection) > 0) {
                    e.setDamage(e.getDamage() * getSkillVariables().getDouble("damage-multiplier", 1.3));
                }

                setRemainingCooldown(getCooldown());
            }
        }
    }

    @Override
    public void initialize() {

    }
}
