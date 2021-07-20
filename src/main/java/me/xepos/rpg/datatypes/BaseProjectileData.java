package me.xepos.rpg.datatypes;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

public class BaseProjectileData implements IClearable {
    private final Projectile projectile;
    private final int shooterLevel;
    private final long despawnTime;
    private boolean summonsLightning;
    private boolean shouldTeleport;
    private final PotionEffect potionEffect;
    private boolean shouldBounce = false;

    public BaseProjectileData(Projectile projectile, int shooterLevel, int secondsToLive){
        this.projectile = projectile;
        this.despawnTime = secondsToLive;
        this.potionEffect = null;
        this.shooterLevel = shooterLevel;
    }

    public BaseProjectileData(Projectile projectile, int shooterLevel, int secondsToLive, PotionEffect potionEffect){
        this.projectile = projectile;
        this.despawnTime = secondsToLive;
        this.potionEffect = potionEffect;
        this.shooterLevel = shooterLevel;
    }


    public Projectile getProjectile() {
        return projectile;
    }


    @Override
    public boolean shouldRemove() {
        return System.currentTimeMillis() >= despawnTime;
    }

    public boolean summonsLightning() {
        return summonsLightning;
    }

    public boolean shouldTeleport() {
        return shouldTeleport;
    }

    public void shouldTeleport(boolean shouldTeleport) {
        this.shouldTeleport = shouldTeleport;
    }

    public void setSummonsLightning(boolean summonsLightning) {
        this.summonsLightning = summonsLightning;
    }


    public void summonCloud() {
        if (potionEffect != null) {
            AreaEffectCloud cloud = projectile.getWorld().spawn(projectile.getLocation(), AreaEffectCloud.class);
            cloud.setSource(projectile.getShooter());
            //PotionEffect(PotionEffectType.HARM, 50, 0
            cloud.addCustomEffect(potionEffect, true);
        }
    }

    public boolean shouldBounce() {
        return shouldBounce;
    }

    public void setShouldBounce(boolean shouldBounce) {
        this.shouldBounce = shouldBounce;
    }

    public Entity getShooter() {
        return (Entity) projectile.getShooter();
    }

    public int getShooterLevel() {
        return shooterLevel;
    }
}
