package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class TrueDefender extends XRPGActiveSkill {
    private boolean isActive = false;
    private boolean tookPlayerDamage = false;

    public TrueDefender(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN_ENVIRONMENTAL").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (isActive) {
            if (event instanceof EntityDamageByEntityEvent e) {

                Player player = (Player) e.getEntity();
                LivingEntity damager = null;
                if (e.getDamager() instanceof Projectile projectile) {
                    if (projectile.getShooter() instanceof LivingEntity) {
                        if (projectile.getShooter() instanceof Player){
                            this.tookPlayerDamage = true;
                        }
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

        } else if (event instanceof PlayerItemHeldEvent e){

            if (!isSkillReady()){
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }else if(!hasRequiredMana()){
                sendNotEnoughManaMessage();
                return;
            }

            this.isActive = true;
            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ITEM_TRIDENT_RETURN, 1F, 1F);

            int duration = (int)(getSkillVariables().getDouble(getSkillLevel(), "duration", 3.0) * 20);
            //BukkitTask task = new ParticleCircleEffectTask(Particle.ELECTRIC_SPARK, e.getPlayer(), 0.35, 40).runTaskTimer(getPlugin(), 0, 1);
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (tookPlayerDamage) {
                    endEffect(e.getPlayer().getLocation());
                }else{
                    if (getSkillVariables().getBoolean(getSkillLevel(), "extend-duration", false)){
                        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                            endEffect(e.getPlayer().getLocation());
                        }, duration);
                    }else{
                        endEffect(e.getPlayer().getLocation());
                    }
                }

                this.tookPlayerDamage = false;
            }, duration);

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }

    @Override
    public void initialize() {

    }

    private void endEffect(Location location){
        //task.cancel();
        this.isActive = false;
        location.getWorld().playSound(location, Sound.ITEM_TRIDENT_RIPTIDE_2, 1F, 1F);
    }
}
