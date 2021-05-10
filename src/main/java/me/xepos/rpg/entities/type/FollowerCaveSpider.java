package me.xepos.rpg.entities.type;

import me.xepos.rpg.entities.Follower;
import me.xepos.rpg.entities.pathfinders.PathfinderGoalLeapAtTarget;
import me.xepos.rpg.entities.pathfinders.PathfinderGoalSpiderMeleeAttack;
import me.xepos.rpg.entities.type.interfaces.IFollowerSpider;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;

@SuppressWarnings("unused")
public class FollowerCaveSpider extends Follower implements IFollowerSpider {
    private static final DataWatcherObject<Byte> b;

    static {
        b = DataWatcher.a(EntitySpider.class, DataWatcherRegistry.a);
    }

    public FollowerCaveSpider(EntityTypes<EntityCaveSpider> type, Location loc, LivingEntity owner) {
        super(type, loc, owner);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();

        this.goalSelector.a(1, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(2, new PathfinderGoalSpiderMeleeAttack(this));
    }

    public boolean attackEntity(Entity entity) {
        if (!super.attackEntity(entity)) {
            return false;
        } else {
            if (entity instanceof EntityLiving) {
                ((EntityLiving)entity).addEffect(new MobEffect(MobEffects.POISON, 100, 0), EntityPotionEffectEvent.Cause.ATTACK);
            }

            return true;
        }
    }

    protected void initDatawatcher() {

        super.initDatawatcher();
        this.datawatcher.register(b, (byte)0);
    }

    public void tick() {
        super.tick();
        if (!this.world.isClientSide) {
            this.t(this.positionChanged);
        }

    }

    public void t(boolean flag) {
        byte b0 = (Byte)this.datawatcher.get(b);
        if (flag) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 &= -2;
        }

        this.datawatcher.set(b, b0);
    }

    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.45F;
    }

    public boolean eL() {
        return ((Byte)this.datawatcher.get(b) & 1) != 0;
    }

    public boolean isClimbing() {
        return this.eL();
    }

    public NavigationAbstract b(World world) {
        return new NavigationSpider(this, world);
    }
}
