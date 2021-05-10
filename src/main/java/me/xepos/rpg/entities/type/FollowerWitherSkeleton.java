package me.xepos.rpg.entities.type;

import me.xepos.rpg.entities.Follower;
import me.xepos.rpg.entities.pathfinders.PathfinderGoalBowShoot;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;


@SuppressWarnings("unused")
public class FollowerWitherSkeleton extends Follower implements IRangedEntity {
    private final me.xepos.rpg.entities.pathfinders.PathfinderGoalBowShoot<FollowerWitherSkeleton> b = new PathfinderGoalBowShoot<FollowerWitherSkeleton>(this, 1.0D, 20, 15.0F);
    private final PathfinderGoalMeleeAttack c = new PathfinderGoalMeleeAttack(this, 1.2D, false);

    public FollowerWitherSkeleton(EntityTypes<EntitySkeletonWither> type, Location loc, LivingEntity owner) {
        super(type, loc, owner);
        this.a(PathType.LAVA, 8.0F);
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();

        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);

    }

    public boolean attackEntity(Entity entity) {
        if (!super.attackEntity(entity)) {
            return false;
        } else {
            if (entity instanceof EntityLiving) {
                ((EntityLiving)entity).addEffect(new MobEffect(MobEffects.WITHER, 200), EntityPotionEffectEvent.Cause.ATTACK);
            }

            return true;
        }
    }

    protected EntityArrow b(ItemStack itemstack, float f) {
        EntityArrow entityarrow = ProjectileHelper.a(this, itemstack, f);
        entityarrow.setOnFire(100);
        return entityarrow;
    }

    public boolean d(MobEffect mobeffect) {
        return mobeffect.getMobEffect() != MobEffects.WITHER && super.d(mobeffect);
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        super.setSlot(enumitemslot, itemstack);
        if (!this.world.isClientSide) {
            this.eL();
        }
    }

    public void eL() {
        if (this.world != null && !this.world.isClientSide) {
            this.goalSelector.a((PathfinderGoal)this.c);
            this.goalSelector.a((PathfinderGoal)this.b);
            ItemStack itemstack = this.b(ProjectileHelper.a(this, Items.BOW));
            if (itemstack.getItem() == Items.BOW) {
                byte b0 = 20;

                this.b.a(b0);
                this.goalSelector.a(1, this.b);
            } else {
                this.goalSelector.a(1, this.c);
            }
        }

    }

    //region Sound
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_WITHER_SKELETON_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_WITHER_SKELETON_DEATH;
    }

    SoundEffect eK() {
        return SoundEffects.ENTITY_WITHER_SKELETON_STEP;
    }

    @Override
    public void a(EntityLiving entityLiving, float v) {

    }

    //endregion
}
