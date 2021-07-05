package me.xepos.rpg.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class LeapOfFaithTask extends BukkitRunnable {
    private final Location location;
    private final byte duration;
    private final PotionEffect levitationEffect;
    private final PotionEffect slowFallingEffect;

    private byte counter = 0;

    public LeapOfFaithTask(Location baseLocation){
        this.location = baseLocation.add(0, 1, 0);
        this.duration = 10;
        this.levitationEffect = new PotionEffect(PotionEffectType.LEVITATION, 200, 1, false, false, false);
        this.slowFallingEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, 400, 0, false, false, true);
    }

    @Override
    public void run() {
        if (counter > duration){
            this.cancel();
            return;
        }
        Collection<Entity> livingEntities = location.getWorld().getNearbyEntities(location, 5, 2, 5, p -> p instanceof LivingEntity);

        for (Entity entity:livingEntities) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.addPotionEffect(levitationEffect);
            livingEntity.addPotionEffect(slowFallingEffect);
        }

        counter++;
    }
}
