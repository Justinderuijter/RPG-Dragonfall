package me.xepos.rpg.entities.type;

import me.xepos.rpg.entities.Follower;
import me.xepos.rpg.entities.pathfinders.PathfinderGoalBlazeFireball;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class FollowerBlaze extends Follower {
    private static final DataWatcherObject<Byte> d;

    static {
        d = DataWatcher.a(EntityBlaze.class, DataWatcherRegistry.a);
    }

    public FollowerBlaze(EntityTypes<EntityBlaze> type, Location loc, LivingEntity owner) {
        super(type, loc, owner);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();

        this.goalSelector.a(1, new PathfinderGoalBlazeFireball(this));
    }

    public void t(boolean var0) {
        byte var1 = (Byte) this.datawatcher.get(d);
        if (var0) {
            var1 = (byte) (var1 | 1);
        } else {
            var1 &= -2;
        }

        this.datawatcher.set(d, var1);
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(d, (byte) 0);
    }

    public boolean isBurning() {
        return this.eK();
    }

    private boolean eK() {
        return ((Byte) this.datawatcher.get(d) & 1) != 0;
    }

}
