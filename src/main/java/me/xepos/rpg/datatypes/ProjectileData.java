package me.xepos.rpg.datatypes;

import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

public class ProjectileData extends BaseProjectileData {
    private double damage;
    private double damageMultiplier = 1.0;
    private int fireTicks;
    private double headshotDamage = 1.0;
    private boolean doDisengage = false;

    public ProjectileData(Projectile projectile, int shooterLevel, int secondsToLive){
        super(projectile, shooterLevel, secondsToLive);
    }

    public ProjectileData(Projectile projectile, int shooterLevel, double damage, int secondsToLive){
        super(projectile, shooterLevel, secondsToLive);

        this.damage = damage;
    }

    public ProjectileData(Projectile projectile, int shooterLevel, int secondsToLive, PotionEffect potionEffect){
        super(projectile, shooterLevel, secondsToLive, potionEffect);
    }

    public ProjectileData(Projectile projectile, int shooterLevel, double damage, int secondsToLive, PotionEffect potionEffect){
        super(projectile, shooterLevel, secondsToLive, potionEffect);

        this.damage = damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
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

    public double getHeadshotDamage() {
        return headshotDamage;
    }

    public void setHeadshotDamage(double headshotDamage) {
        this.headshotDamage = headshotDamage;
    }

    public boolean shouldDisengage() {
        return doDisengage;
    }

    public void setDisengage(boolean doDisengage) {
        this.doDisengage = doDisengage;
    }

}
