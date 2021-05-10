package me.xepos.rpg.entities.type;

import me.xepos.rpg.entities.Follower;
import me.xepos.rpg.entities.pathfinders.PathfinderGoalBowShoot;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;


@SuppressWarnings("all")
public class FollowerSkeleton extends Follower implements IRangedEntity {
    private final PathfinderGoalBowShoot<FollowerSkeleton> b = new PathfinderGoalBowShoot<FollowerSkeleton>(this, 1.0D, 20, 15.0F);
    private final PathfinderGoalMeleeAttack c = new PathfinderGoalMeleeAttack(this, 1.2D, false);


    public FollowerSkeleton(EntityTypes<EntitySkeleton> type, Location loc, LivingEntity owner) {
        super(type, loc, owner);
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    protected void initPathfinder()
    {
        super.initPathfinder();
        this.goalSelector.a(1, new PathfinderGoalBowShoot<FollowerSkeleton>(this, 1.0D, 20, 15.0F));
    }

    @Override
    public void a(EntityLiving entityLiving, float v) {
        //We need this to make the skeleton stop attacking.
        //Or else it will keep shooting the same spot forever.
        //Null check might not be needed but adding it anyway since
        //it took me like an entire day to fix this stuff.
        if (entityLiving == null || entityLiving.getHealth() <= 0F){
            this.setGoalTarget(null, EntityTargetEvent.TargetReason.TARGET_DIED, false);
            return;
        }


        ItemStack itemstack = this.f(this.b(ProjectileHelper.a(this, Items.BOW)));
        EntityArrow entityarrow = this.b(itemstack, (float)f);
        double d0 = entityLiving.locX() - this.locX();
        double d1 = entityLiving.e(0.3333333333333333D) - entityarrow.locY();
        double d2 = entityLiving.locZ() - this.locZ();
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.world.getDifficulty().a() * 4));
        EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(this, this.getItemInMainHand(), (ItemStack)null, entityarrow, EnumHand.MAIN_HAND, 0.8F, true);
        if (event.isCancelled()) {
            event.getProjectile().remove();
        } else {
            if (event.getProjectile() == entityarrow.getBukkitEntity()) {
                this.world.addEntity(entityarrow);
            }

            this.playSound(SoundEffects.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    public boolean a(ItemProjectileWeapon itemprojectileweapon) {
        return itemprojectileweapon == Items.BOW;
    }

    protected EntityArrow b(ItemStack itemstack, float f) {
        return ProjectileHelper.a(this, itemstack, f);
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

    public void movementTick() {
        super.movementTick();
    }


    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        System.out.println("setSlot");
        super.setSlot(enumitemslot, itemstack);
        if (!this.world.isClientSide) {
            this.eL();
        }
    }

    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 1.74F;
    }

    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.eK(), 0.15F, 1.0F);
    }

    public double bb() {
        return -0.6D;
    }

    SoundEffect eK() {
        return SoundEffects.ENTITY_SKELETON_STEP;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }


}