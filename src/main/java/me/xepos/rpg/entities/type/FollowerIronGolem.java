package me.xepos.rpg.entities.type;

import me.xepos.rpg.entities.Follower;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@SuppressWarnings("unused")
public class FollowerIronGolem extends Follower {
    private int c;
    private int d;

    public FollowerIronGolem(EntityTypes<? extends EntityCreature> type, Location loc, LivingEntity owner) {
        super(type, loc, owner);
        this.G = 1.0F;
    }

    @Override
    protected void initPathfinder()
    {
        super.initPathfinder();

        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    }

    public boolean attackEntity(Entity entity) {
        this.c = 10;
        this.world.broadcastEntityEffect(this, (byte)4);
        float f = this.eO();
        float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f1);
        if (flag) {
            entity.setMot(entity.getMot().add(0.0D, 0.6D, 0.0D));
            this.a(this, entity);
        }

        this.playSound(SoundEffects.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    public void movementTick() {
        super.movementTick();
        if (this.c > 0) {
            --this.c;
        }

        if (this.d > 0) {
            --this.d;
        }

        if (c(this.getMot()) > 2.500000277905201E-7D && this.random.nextInt(5) == 0) {
            int i = MathHelper.floor(this.locX());
            int j = MathHelper.floor(this.locY() - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ());
            IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));
            if (!iblockdata.isAir()) {
                this.world.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getWidth(), this.locY() + 0.1D, this.locZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getWidth(), 4.0D * ((double)this.random.nextFloat() - 0.5D), 0.5D, ((double)this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        EntityIronGolem.CrackLevel entityirongolem_cracklevel = this.eK();
        boolean flag = super.damageEntity(damagesource, f);
        if (flag && this.eK() != entityirongolem_cracklevel) {
            this.playSound(SoundEffects.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }

        return flag;
    }

    public EntityIronGolem.CrackLevel eK() {
        return EntityIronGolem.CrackLevel.a(this.getHealth() / this.getMaxHealth());
    }

    private float eO() {
        return (float)this.b(GenericAttributes.ATTACK_DAMAGE);
    }

    public void t(boolean flag) {
        if (flag) {
            this.d = 400;
            this.world.broadcastEntityEffect(this, (byte)11);
        } else {
            this.d = 0;
            this.world.broadcastEntityEffect(this, (byte)34);
        }

    }

    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_IRON_GOLEM_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_IRON_GOLEM_DEATH;
    }

}
