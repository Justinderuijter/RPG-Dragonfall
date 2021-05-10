package me.xepos.rpg.datatypes;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

public class BaseProjectileData implements IClearable {
    private final Projectile projectile;
    private final long despawnTime;
    private boolean summonsLightning;
    private final boolean shouldTeleport;
    private final PotionEffect potionEffect;
    private boolean shouldBounce = false;

    public BaseProjectileData(Projectile projectile, boolean summonLightning, boolean teleportShooter, int secondsToLive, PotionEffect potionEffect) {
        this.projectile = projectile;
        this.summonsLightning = summonLightning;
        this.shouldTeleport = teleportShooter;
        this.despawnTime = System.currentTimeMillis() + secondsToLive * 20L;
        this.potionEffect = potionEffect;
    }

    public BaseProjectileData(Projectile projectile, boolean summonLightning, boolean teleportShooter, int secondsToLive) {
        this.projectile = projectile;
        this.summonsLightning = summonLightning;
        this.shouldTeleport = teleportShooter;
        this.despawnTime = System.currentTimeMillis() + secondsToLive * 20L;
        this.potionEffect = null;
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

    public void setSummonsLightning(boolean summonsLightning) {
        this.summonsLightning = summonsLightning;
    }

    public boolean shouldTeleport() {
        return this.shouldTeleport;
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
}
