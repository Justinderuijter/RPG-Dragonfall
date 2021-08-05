package me.xepos.rpg.events;

import me.xepos.rpg.datatypes.ProjectileData;

public class XRPGProjectileFireEvent extends XRPGBaseProjectileFireEvent{
    private final ProjectileData projectileData;

    public XRPGProjectileFireEvent(ProjectileData projectileData) {
        super(projectileData);
        this.projectileData = projectileData;
    }

    public void setDamage(double damage) {
        this.projectileData.setDamage(damage);
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.projectileData.setDamageMultiplier(damageMultiplier);
    }

    public void setFireTicks(int fireTicks) {
        this.projectileData.setFireTicks(fireTicks);
    }

    public double getDamage() {
        return projectileData.getDamage();
    }

    public double getDamageMultiplier() {
        return projectileData.getDamageMultiplier();
    }

    public int getFireTicks() {
        return projectileData.getFireTicks();
    }

    public double getHeadshotDamage() {
        return projectileData.getHeadshotDamage();
    }

    public void setHeadshotDamage(double headshotDamage) {
        this.projectileData.setHeadshotDamage(headshotDamage);
    }

    public boolean shouldDisengage() {
        return projectileData.shouldDisengage();
    }

    public void setDisengage(boolean doDisengage) {
        this.projectileData.setDisengage(doDisengage);
    }
}
