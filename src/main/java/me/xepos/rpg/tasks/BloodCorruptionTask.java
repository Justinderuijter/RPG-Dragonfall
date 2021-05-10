package me.xepos.rpg.tasks;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class BloodCorruptionTask extends BukkitRunnable {

    private final Location originalLocation;
    private final LivingEntity target;
    private final LivingEntity caster;
    private final double damage;

    public BloodCorruptionTask(LivingEntity caster, LivingEntity target, double damage) {
        this.originalLocation = target.getLocation().clone();
        this.target = target;
        this.caster = caster;
        this.damage = damage;
    }

    @Override
    public void run() {
        double distance = 0;
        if (originalLocation.getWorld() == target.getWorld()) {
            distance = target.getLocation().distance(originalLocation);
        }
        target.damage(distance * damage, caster);
    }
}
