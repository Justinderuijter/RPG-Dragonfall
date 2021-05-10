package me.xepos.rpg.entities.type;

import me.xepos.rpg.entities.Follower;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@SuppressWarnings("unused")
public class FollowerEnderman extends Follower {
    public FollowerEnderman(EntityTypes<? extends EntityCreature> type, Location loc, LivingEntity owner) {
        super(type, loc, owner);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();

        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, false));
    }

    //Called when this entity takes damage
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!(damagesource instanceof EntityDamageSourceIndirect)) {
            boolean flag = super.damageEntity(damagesource, f);
            if (!this.world.s_() && !(damagesource.getEntity() instanceof EntityLiving) && this.random.nextInt(10) != 0) {
                this.eL();
            }

            return flag;
        } else {
            for(int i = 0; i < 64; ++i) {
                if (this.eL()) {
                    return true;
                }
            }

            return false;
        }
    }

    //look for location to teleport to
    protected boolean eL() {
        if (!this.world.s_() && this.isAlive()) {
            double d0 = this.locX() + (this.random.nextDouble() - 0.5D) * 32.0D;
            double d1 = this.locY() + (double)(this.random.nextInt(6) - 3); //can go up or down 3 blocks
            double d2 = this.locZ() + (this.random.nextDouble() - 0.5D) * 32.0D;
            return this.p(d0, d1, d2);
        } else {
            return false;
        }
    }

    //Check if location is valid, then teleport if it is
    private boolean p(double d0, double d1, double d2) {
        BlockPosition.MutableBlockPosition mutableBlockPosition = new BlockPosition.MutableBlockPosition(d0, d1, d2);

        while(mutableBlockPosition.getY() > 0 && !this.world.getType(mutableBlockPosition).getMaterial().isSolid()) {
            mutableBlockPosition.c(EnumDirection.DOWN);
        }

        IBlockData iblockdata = this.world.getType(mutableBlockPosition);
        boolean flag = iblockdata.getMaterial().isSolid();
        boolean flag1 = iblockdata.getFluid().a(TagsFluid.WATER);
        if (flag && !flag1) {
            boolean flag2 = this.a(d0, d1, d2, true);
            if (flag2 && !this.isSilent()) {
                this.world.playSound((EntityHuman)null, this.lastX, this.lastY, this.lastZ, SoundEffects.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEffects.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }
}
