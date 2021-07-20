package me.xepos.rpg.tasks.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleCircleEffectTask extends BukkitRunnable {
    private final Particle particle;
    private final LivingEntity livingEntity;
    private final int amount;
    private final double radius;

    public ParticleCircleEffectTask(Particle particle, LivingEntity livingEntity, double radius, int amount){
        this.particle = particle;
        this.livingEntity = livingEntity;
        this.amount = amount;
        this.radius = radius;
    }

    @Override
    public void run() {
        Location location = livingEntity.getEyeLocation().add(0, 0.5, 0);
        for (int degree = 0; degree < 360; degree++) {
            double radians = Math.toRadians(degree);
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;

            location.add(x,0,z);
            location.getWorld().spawnParticle(particle, location, 0, 0, 0, 0, 0.01);
            location.subtract(x,0,z);
        }
    }
}
