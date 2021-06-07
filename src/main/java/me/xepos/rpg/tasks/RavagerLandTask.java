package me.xepos.rpg.tasks;

import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RavagerLandTask extends BukkitRunnable {

    private final Player player;
    private final double damage;
    private final ProtectionSet protectionSet;
    private final PartySet partyManager;

    public RavagerLandTask(Player player, double damage, ProtectionSet protectionSet, PartySet partyManager) {
        this.player = player;
        this.damage = damage;
        this.protectionSet = protectionSet;
        this.partyManager = partyManager;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }

        if (!player.isFlying() && player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid())
        {
            List<Entity> entities =  new ArrayList<>(player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 4, 2, 4, p -> p instanceof LivingEntity && p != player));
            for (Entity entity:entities) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity instanceof Player) {
                    if (protectionSet.isLocationValid(player.getLocation(), livingEntity.getLocation()) && !partyManager.isPlayerAllied(player, (Player) livingEntity)) {
                        damageAndSlowTarget(livingEntity);
                    }
                } else {
                    damageAndSlowTarget(livingEntity);
                }
            }
            this.cancel();
        }
    }

    private void damageAndSlowTarget(LivingEntity livingEntity) {
        livingEntity.damage(damage, player);
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, false, false, true));
    }
}
