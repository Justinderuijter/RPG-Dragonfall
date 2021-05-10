package me.xepos.rpg.datatypes;

import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

public class ExplosiveProjectileData extends BaseProjectileData {
    private final float yield;
    private final boolean destroysBlocks;
    private final boolean setsFire;

    public ExplosiveProjectileData(Projectile projectile, float yield, boolean destroyBlocks, boolean setFire, boolean summonLightning, boolean teleportShooter, int secondsToLive, PotionEffect potionEffect) {
        super(projectile, summonLightning, teleportShooter, secondsToLive, potionEffect);

        this.yield = yield;
        this.destroysBlocks = destroyBlocks;
        this.setsFire = setFire;
    }

    public ExplosiveProjectileData(Projectile projectile, float yield, boolean destroyBlocks, boolean setFire, int secondsToLive, PotionEffect potionEffect) {
        super(projectile, false, false, secondsToLive, potionEffect);

        this.yield = yield;
        this.destroysBlocks = destroyBlocks;
        this.setsFire = setFire;
    }

    public ExplosiveProjectileData(Projectile projectile, float yield, boolean destroyBlocks, boolean setFire, boolean summonLightning, boolean teleportShooter, int secondsToLive) {
        super(projectile, summonLightning, teleportShooter, secondsToLive);

        this.yield = yield;
        this.destroysBlocks = destroyBlocks;
        this.setsFire = setFire;
    }

    public ExplosiveProjectileData(Projectile projectile, float yield, boolean destroyBlocks, boolean setFire, int secondsToLive) {
        super(projectile, false, false, secondsToLive);

        this.yield = yield;
        this.destroysBlocks = destroyBlocks;
        this.setsFire = setFire;
    }


    public float getYield() {
        return yield;
    }

    public boolean destroysBlocks() {
        return destroysBlocks;
    }

    public boolean setsFire() {
        return setsFire;
    }
}
