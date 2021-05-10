package me.xepos.rpg.tasks;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BleedTask extends BukkitRunnable {

    private final LivingEntity target;
    private final Player source;
    private final double damage;
    private byte triggers = 0;
    private final byte maxTriggers;

    public BleedTask(LivingEntity target, @Nullable Player source, byte maxTriggers, double damagePerTrigger) {
        this.target = target;
        this.source = source;
        this.damage = damagePerTrigger;
        this.maxTriggers = maxTriggers;
    }

    @Override
    public void run() {
        if (triggers >= maxTriggers)
        {
            this.cancel();
            return;
        }

        if (target instanceof Player && !((Player)target).isOnline())
        {
            this.cancel();
            return;
        }

        if (target != null) {
            if (source == null)
                target.damage(damage);
            else
                target.damage(damage, source);
        }
        triggers++;
    }
}
