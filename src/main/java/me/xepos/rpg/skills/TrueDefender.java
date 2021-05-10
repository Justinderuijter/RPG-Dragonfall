package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TrueDefender extends XRPGSkill {
    private boolean isActive = false;

    public TrueDefender(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    @Override
    public void activate(Event event) {
        if (isActive) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

                Player player = (Player) e.getEntity();
                LivingEntity damager = (LivingEntity) e.getDamager();

                damager.setNoDamageTicks(0);
                damager.damage(e.getDamage(), player);
            } else if (event instanceof EntityDamageEvent) {
                ((EntityDamageEvent) event).setCancelled(true);

            }
        } else if (event instanceof PlayerInteractEvent){

            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if (!isSkillReady()){
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }
            this.isActive = true;

            int duration = (int)(getSkillVariables().getDouble("duration", 3.0) * 20);
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> this.isActive = false, duration);

            setRemainingCooldown(getCooldown());
        }
    }

    @Override
    public void initialize() {

    }
}
