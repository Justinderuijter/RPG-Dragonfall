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

    int currentTicks = 0;
    double var = 0;
    Location loc, first, second;

    public ParticleSpiralEffectTask(Particle particle, Player player, double radius, int amount, double duration){
        this.particle = particle;
        this.player = player;
        this.duration = (long)(duration * 20);
        this.radius = radius;
        this.amount = amount;
    }


    @Override
    public void run() {
        if (player.isValid() && currentTicks <= duration) {
            var += Math.PI / 16;

            loc = player.getLocation();
            first = loc.clone().add(Math.cos(var) * radius, Math.sin(var) + 1, Math.sin(var) * radius);
            second = loc.clone().add(Math.cos(var + Math.PI) * radius, Math.sin(var) + 1, Math.sin(var + Math.PI) * radius);

            player.getWorld().spawnParticle(particle, first, 0);
            player.getWorld().spawnParticle(particle, second, 0);

            currentTicks++;
        }else{
            this.cancel();
        }
    }
}
