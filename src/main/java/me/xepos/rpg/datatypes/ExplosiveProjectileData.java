package me.xepos.rpg.datatypes;

import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

public class ExplosiveProjectileData extends BaseProjectileData {
    private float yield;
    private boolean destroysBlocks = false;
    private boolean setsFire = false;

    public ExplosiveProjectileData(Projectile projectile, int shooterLevel, float yield, int secondsToLive){
        super(projectile, shooterLevel, secondsToLive);

        this.yield = yield;
    }

    public ExplosiveProjectileData(Projectile projectile, int shooterLevel, float yield, int secondsToLive, PotionEffect potionEffect){
        super(projectile, shooterLevel, secondsToLive, potionEffect);

        this.yield = yield;
    }

    public void setYield(float yield) {
        this.yield = yield;
    }

    public boolean destroysBlocks() {
        return destroysBlocks;
    }

    public void destroysBlocks(boolean destroysBlocks) {
        this.destroysBlocks = destroysBlocks;
    }

    public boolean setsFire() {
        return setsFire;
    }

    public void setsFire(boolean setsFire) {
        this.setsFire = setsFire;
    }

    public float getYield() {
        return yield;
    }

}
