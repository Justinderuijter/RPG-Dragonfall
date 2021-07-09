package me.xepos.rpg.tasks;

import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HealOverTimeTask extends BukkitRunnable {
    private byte count = 0;
    private final LivingEntity entity;
    private final int maxProcs;
    private final double healPerProc;

    public HealOverTimeTask(LivingEntity entity, double healPerProc, int maxProcs) {
        this.entity = entity;
        this.maxProcs = maxProcs;
        this.healPerProc = healPerProc;
    }

    @Override
    public void run() {
        if (entity instanceof Player && !((Player) entity).isOnline())
            this.cancel();

        if (count < maxProcs) {
            if (entity.isValid() && !entity.isDead()) {
                Utils.healLivingEntity(entity, healPerProc);
            }
            count++;
        } else
            this.cancel();
    }
}
