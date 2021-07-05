package me.xepos.rpg.tasks.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleSpiralEffectTask extends BukkitRunnable {
    private final Particle particle;
    private final Player player;
    private final long duration;
    private final double radius;
    private final int amount;
    private final boolean updateLocation;
    private final double extra;

    int currentTicks = 0;
    double var = 0;
    Location loc, first, second;

    public ParticleSpiralEffectTask(Particle particle, Player player, double radius, int amount, double durationInSeconds, boolean updateLocation, double extra){
        this.particle = particle;
        this.player = player;
        this.duration = (long)(durationInSeconds * 20);
        this.radius = radius;
        this.amount = amount;
        this.updateLocation = updateLocation;
        this.extra = extra;
        if (!updateLocation) loc = player.getLocation();
    }


    @Override
    public void run() {
        if (player.isValid() && currentTicks <= duration) {
            var += Math.PI / 16;

            if (updateLocation) loc = player.getLocation();

            first = loc.clone().add(Math.cos(var) * radius, Math.sin(var) + 1, Math.sin(var) * radius);
            second = loc.clone().add(Math.cos(var + Math.PI) * radius, Math.sin(var) + 1, Math.sin(var + Math.PI) * radius);

            player.getWorld().spawnParticle(particle, first, amount, 0, 0, 0, extra);
            player.getWorld().spawnParticle(particle, second, amount, 0, 0, 0, extra);

            currentTicks++;
        }else{
            this.cancel();
        }
    }
}
