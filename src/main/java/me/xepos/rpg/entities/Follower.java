package me.xepos.rpg.entities;

import me.xepos.rpg.entities.pathfinders.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public abstract class Follower extends EntityCreature {
    private LivingEntity owner;

    public Follower(EntityTypes<? extends EntityCreature> type, Location loc, LivingEntity owner) {
        super(type, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.owner = owner;
    }

    @Override
    protected void initPathfinder()
    {
        double base = this.getAttributeInstance(GenericAttributes.MAX_HEALTH).getBaseValue();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(base * 2);
        this.setHealth((float) base * 2);
        System.out.println(this.getAttributeInstance(GenericAttributes.MAX_HEALTH).getValue());

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, new PathfinderGoalFollowOwner(this, 1, 2F, 16F, true));
        this.goalSelector.a(4, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomLookaround(this));
    }


    public LivingEntity getOwner() {
        return owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }
}
