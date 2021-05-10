package me.xepos.rpg.entities.type;

import me.xepos.rpg.entities.Follower;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class FollowerZombie extends Follower {
    public FollowerZombie(EntityTypes<EntityZombie> type, Location loc, LivingEntity owner) {
        super(type, loc, owner);
        this.setCanPickupLoot(true);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();

        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, false));
    }

    @Override
    public boolean attackEntity(Entity entity) {
        if (!super.attackEntity(entity)) {
            return false;
        } else {
            if (entity instanceof EntityLiving) {
                ((EntityLiving)entity).addEffect(new MobEffect(MobEffects.HUNGER, 200), EntityPotionEffectEvent.Cause.ATTACK);
            }

            return true;
        }
    }

}
