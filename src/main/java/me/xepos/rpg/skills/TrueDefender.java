package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class TrueDefender extends XRPGActiveSkill {
    private boolean isActive = false;

    public TrueDefender(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN_ENVIRONMENTAL").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (isActive) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

                Player player = (Player) e.getEntity();
                LivingEntity damager = null;
                if (e.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) e.getDamager();
                    if (projectile.getShooter() instanceof LivingEntity) {
                        damager = (LivingEntity) projectile.getShooter();
                    }
                } else {
                    damager = (LivingEntity) e.getDamager();
                }

                if (damager != null) {
                    damager.setNoDamageTicks(0);
                    damager.damage(e.getDamage(), player);
                }
            }
            ((Cancellable)event).setCancelled(true);

        } else if (event instanceof PlayerItemHeldEvent){

            PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
            if (!isSkillReady()){
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }else if(!hasRequiredMana()){
                sendNotEnoughManaMessage();
                return;
            }

            this.isActive = true;

            int duration = (int)(getSkillVariables().getDouble(getSkillLevel(), "duration", 3.0) * 20);
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> this.isActive = false, duration);

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }

    @Override
    public void initialize() {

    }
}
