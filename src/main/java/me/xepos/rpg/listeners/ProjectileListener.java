package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.datatypes.BaseProjectileData;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class ProjectileListener implements Listener {
    private final XRPG plugin;
    private final ProtectionSet ps;

    public ProjectileListener(XRPG plugin) {
        this.plugin = plugin;
        this.ps = plugin.getProtectionSet();
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent e) {
        if (e.getHitBlock() == null) return;
        if (!plugin.projectiles.containsKey(e.getEntity().getUniqueId())) return;
        final Projectile projectile = e.getEntity();

        BaseProjectileData pData = plugin.projectiles.get(projectile.getUniqueId());

        if (pData.getProjectile() instanceof Explosive && !(pData.getProjectile() instanceof SmallFireball)) {
            //We use explosion prime event instead
            return;
        }

        //Only triggers if potion effect is added to the data
        pData.summonCloud();

        if (pData instanceof ProjectileData projectileData) {
            if (projectileData.summonsLightning()) {
                e.getHitBlock().getWorld().strikeLightning(e.getHitBlock().getLocation());
            }

            if (projectileData.shouldTeleport()) {
                projectileData.getShooter().teleport(e.getHitBlock().getLocation().add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }

            if (projectileData.shouldDisengage()) {
                Player shooter = (Player) projectile.getShooter();
                Vector unitVector = shooter.getLocation().toVector().subtract(e.getHitBlock().getLocation().toVector()).normalize();

                shooter.setVelocity(unitVector.multiply(1.5));
            }

        } else if (pData instanceof ExplosiveProjectileData explosiveData) {
            Location location = e.getHitBlock().getLocation();

            //actual execution of skill
            if (explosiveData.summonsLightning()) {
                location.getWorld().strikeLightning(location);
            }

            if (explosiveData.shouldTeleport() && explosiveData.getProjectile().getShooter() instanceof Entity) {
                explosiveData.getShooter().teleport(location);
            }

            location.getWorld().createExplosion(location, explosiveData.getYield(), explosiveData.setsFire(), explosiveData.destroysBlocks(), explosiveData.getShooter());

            if (explosiveData.getProjectile() instanceof Arrow) {
                explosiveData.getProjectile().remove();
            }
        }

        plugin.projectiles.remove(projectile.getUniqueId());
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        if (!plugin.projectiles.containsKey(e.getEntity().getUniqueId())) return;
        ExplosiveProjectileData explosiveData = (ExplosiveProjectileData) plugin.projectiles.get(e.getEntity().getUniqueId());

        Location location = explosiveData.getProjectile().getLocation();
        e.setCancelled(true);
        location.getWorld().createExplosion(location, explosiveData.getYield(), explosiveData.setsFire(), explosiveData.destroysBlocks(), explosiveData.getShooter());

        plugin.projectiles.remove(explosiveData.getProjectile().getUniqueId());

    }
}
