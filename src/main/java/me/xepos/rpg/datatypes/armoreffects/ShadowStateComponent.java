package me.xepos.rpg.datatypes.armoreffects;

import me.xepos.rpg.XRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ShadowStateComponent implements IEffectComponent {
    private final PotionEffect effect;
    private final int duration;

    private boolean isActive;

    public ShadowStateComponent(String effect) {
        this.isActive = false;
        int duration = 100;

        String[] args = effect.split(":");
        if (args.length > 1){
            duration = Integer.parseInt(args[1]);
        }

        this.duration = duration;
        this.effect = new PotionEffect(PotionEffectType.BLINDNESS, duration, 1);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getEntity() instanceof Player player) {
                if (!isActive) {
                    XRPG plugin = XRPG.getInstance();
                    this.isActive = true;

                    resetNearbyAggro(player);
                    plugin.getPlayerManager().hidePlayer(player);
                    player.addPotionEffect(effect);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        plugin.getPlayerManager().unhidePlayer(player);
                        this.isActive = false;
                    }, this.duration);
                } else {
                    e.setCancelled(true);
                }
            }
        } else if (event instanceof PlayerDeathEvent e) {
            if (!isActive) {
                XRPG plugin = XRPG.getInstance();
                this.isActive = true;

                resetNearbyAggro(e.getEntity());
                e.setCancelled(true);

                plugin.getPlayerManager().hidePlayer(e.getEntity());
                e.getEntity().addPotionEffect(effect);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    plugin.getPlayerManager().unhidePlayer(e.getEntity());
                    this.isActive = false;
                }, this.duration);
            }
        }
    }

    @SuppressWarnings("all")
    private void resetNearbyAggro(Player player){
        List<Mob> mobList = (List<Mob>) new ArrayList(player.getWorld().getNearbyEntities(player.getLocation(), 15, 15, 15, p -> p instanceof Mob));
        for (Mob mob:mobList) {
            if (mob.getTarget() == player){
                mob.setTarget(null);
            }
        }
    }
}
