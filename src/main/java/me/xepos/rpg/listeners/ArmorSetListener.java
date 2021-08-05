package me.xepos.rpg.listeners;

import me.xepos.rpg.events.XRPGBaseProjectileFireEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class ArmorSetListener implements Listener {

    //Trigger before main event handler
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event){
        if (event instanceof EntityDamageByEntityEvent e){
            if (e.getEntity() instanceof Player player){
                if (e.getDamager() instanceof LivingEntity){
                    //Player is defending
                }else if (e.getDamager() instanceof Projectile){
                    //Player is defending from projectile
                }
            }

            if (e.getDamager() instanceof Player player){
                //Player is attacking
            }
        }else{
            //Natural damage
        }
    }

    //Trigger after main Event handler
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent e){
        if (!(e.getEntity() instanceof Player player)) return;

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCustomProjectile(XRPGBaseProjectileFireEvent e){

    }


}
