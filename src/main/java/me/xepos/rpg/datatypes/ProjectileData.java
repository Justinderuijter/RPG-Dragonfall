package me.xepos.rpg.datatypes;

import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

public class ProjectileData extends BaseProjectileData {
    private final double damage;
    private final double damageMultiplier;
    private final int fireTicks;

    public ProjectileData(Projectile projectile, double damage, double damageMultiplier, int fireTicks, boolean summonLightning, boolean teleportShooter, int secondsToLive, PotionEffect potionEffect) {
        super(projectile, summonLightning, teleportShooter, secondsToLive, potionEffect);

        this.damage = damage;
        this.damageMultiplier = damageMultiplier;
        this.fireTicks = fireTicks;
    }

    public ProjectileData(Projectile projectile, double damage, int fireTicks, boolean summonLightning, boolean teleportShooter, int secondsToLive) {
        super(projectile, summonLightning, teleportShooter, secondsToLive);

        this.damage = damage;
        this.damageMultiplier = 1.0;
        this.fireTicks = fireTicks;
    }

    public ProjectileData(Projectile projectile, double damage, double damageMultiplier, boolean summonLightning, boolean teleportShooter, int secondsToLive, PotionEffect potionEffect) {
        super(projectile, summonLightning, teleportShooter, secondsToLive, potionEffect);

        this.damage = damage;
        this.damageMultiplier = damageMultiplier;
        this.fireTicks = -1;
    }

    public ProjectileData(Projectile projectile, double damage, boolean summonLightning, boolean teleportShooter, int secondsToLive) {
        super(projectile, summonLightning, teleportShooter, secondsToLive);

        this.damage = damage;
        this.damageMultiplier = 1.0;
        this.fireTicks = -1;
    }

    public ProjectileData(Projectile projectile, boolean summonLightning, boolean teleportShooter, int secondsToLive) {
        super(projectile, summonLightning, teleportShooter, secondsToLive);

        this.damage = 0;
        this.damageMultiplier = 1.0;
        this.fireTicks = -1;
    }

    public ProjectileData(Projectile projectile, boolean summonLightning, boolean teleportShooter, int secondsToLive, PotionEffect potionEffect) {
        super(projectile, summonLightning, teleportShooter, secondsToLive, potionEffect);

        this.damage = 0;
        this.damageMultiplier = 1.0;
        this.fireTicks = -1;
    }

    public ProjectileData(Projectile projectile, double damageMultiplier, int secondsToLive) {
        super(projectile, false, false, secondsToLive);

        this.damage = 0;
        this.damageMultiplier = damageMultiplier;
        this.fireTicks = -1;
    }


    public double getDamage() {
        return damage;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public int getFireTicks() {
        return fireTicks;
    }

}
