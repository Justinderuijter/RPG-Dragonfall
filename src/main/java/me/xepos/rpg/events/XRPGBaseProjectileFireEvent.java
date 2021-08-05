package me.xepos.rpg.events;

import me.xepos.rpg.datatypes.BaseProjectileData;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGBaseProjectileFireEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final BaseProjectileData projectileData;

    public XRPGBaseProjectileFireEvent(BaseProjectileData projectileData){
        this.projectileData = projectileData;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Projectile getProjectile(){
        return projectileData.getProjectile();
    }

    public int getShooterLevel(){
        return projectileData.getShooterLevel();
    }

    public void setTeleport(boolean shouldTeleport){
        projectileData.shouldTeleport(shouldTeleport);
    }

    public boolean willTeleport(){
        return projectileData.shouldTeleport();
    }

    public boolean summonsLightning(){
        return projectileData.summonsLightning();
    }

    public boolean setLightning(boolean summonsLightning){
        return projectileData.summonsLightning();
    }

    public boolean willBounce(){
        return projectileData.shouldBounce();
    }

    public void setBounce(boolean bounce){
        projectileData.setShouldBounce(bounce);
    }

    public boolean shouldRemove(){
        return projectileData.shouldRemove();
    }
}
