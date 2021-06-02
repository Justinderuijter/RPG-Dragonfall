package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.datatypes.BaseProjectileData;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
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
        if (!plugin.projectiles.containsKey(e.getEntity().getUniqueId())) return;
        final Projectile projectile = e.getEntity();

        BaseProjectileData pData = plugin.projectiles.get(projectile.getUniqueId());

        if (pData.getProjectile() instanceof Explosive && !(pData.getProjectile() instanceof SmallFireball)) {
            //We use explosion prime event instead
            return;
        }

        plugin.projectiles.remove(e.getEntity().getUniqueId());

        //Only triggers if potion effect is added to the data
        pData.summonCloud();

        if (pData instanceof ProjectileData) {
            ProjectileData projectileData = (ProjectileData) pData;


            if (e.getHitEntity() != null) {
                //Section for all projectiles
                e.getHitEntity().setFireTicks(projectileData.getFireTicks());
                if (projectileData.summonsLightning()) {
                    e.getHitEntity().getWorld().strikeLightning(e.getEntity().getLocation());
                }

                if (projectileData.shouldTeleport()) {
                    projectileData.getShooter().teleport(e.getEntity(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }

                projectileBounceLogic(e, projectileData);

                //Section exclusively for projectiles that aren't arrows
                if (!(e.getEntity() instanceof Arrow)) {
                    if (e.getHitEntity() instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) e.getHitEntity();
                        livingEntity.damage(projectileData.getDamage() * projectileData.getDamageMultiplier(), (Player) projectileData.getProjectile().getShooter());
                    }
                    return;
                }

                //section exclusively for arrows.
                if (e.getHitEntity() instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) e.getHitEntity();
                    if (projectileData.getDamageMultiplier() < 1.0) {

                        livingEntity.setHealth(livingEntity.getHealth() * projectileData.getDamageMultiplier());
                    }

                    if (projectileData.getDamage() != 0) {
                        Utils.decreaseHealth(livingEntity, projectileData.getDamage());
                        livingEntity.setNoDamageTicks(1);
                        projectile.remove();
                    }

                    if (projectileData.getHeadshotDamage() != 1.0) {
                        if (projectile.getLocation().getY() - e.getHitEntity().getLocation().getY() > 1.4D) {
                            Arrow arrow = (Arrow) projectile;
                            double damage = arrow.getDamage() * projectileData.getHeadshotDamage();
                            double damageDifference = damage - arrow.getDamage();
                            arrow.setDamage(damage);

                            ((Player) arrow.getShooter()).sendMessage(ChatColor.DARK_GREEN + "You headshot " + e.getHitEntity().getName() + " and dealt " + damageDifference + " bonus damage!");
                        }
                    }

                    if (projectileData.shouldDisengage()) {
                        Player shooter = (Player) projectile.getShooter();
                        Vector unitVector = shooter.getLocation().toVector().subtract(livingEntity.getLocation().toVector()).normalize();

                        shooter.setVelocity(unitVector.multiply(1.5));
                    }
                }

            } else if (e.getHitBlock() != null) {

                if (projectileData.summonsLightning()) {
                    e.getHitBlock().getWorld().strikeLightning(e.getHitBlock().getLocation());
                }

                if (projectileData.shouldTeleport()) {
                    projectileData.getShooter().teleport(e.getHitBlock().getLocation().add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }

        } else if (pData instanceof ExplosiveProjectileData) {
            ExplosiveProjectileData explosiveData = (ExplosiveProjectileData) pData;

            //Determining location
            Location location = null;
            if (e.getHitBlock() != null) {
                location = e.getHitBlock().getLocation();
            } else if (e.getHitEntity() != null) {
                location = e.getHitEntity().getLocation();
            }

            if (location == null || location.getWorld() == null) return;


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


    private void projectileBounceLogic(ProjectileHitEvent e, ProjectileData data) {
        if (e.getHitBlock() != null) {
            plugin.projectiles.remove(data.getProjectile().getUniqueId());
            return;
        }

        if (data.shouldBounce()) {
            if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) e.getHitEntity();

                livingEntity.damage(data.getDamage(), data.getShooter());
                LivingEntity newTarget = Utils.getRandomLivingEntity(livingEntity, 20.0, 4.0, data.getShooter(), true);
                if (newTarget != null) {
                    Vector vector = newTarget.getLocation().toVector().subtract(livingEntity.getLocation().toVector());
                    Projectile newProjectile = livingEntity.launchProjectile(data.getProjectile().getClass(), vector.normalize());
                    newProjectile.setShooter(data.getProjectile().getShooter());

                    if (!plugin.projectiles.containsKey(newProjectile.getUniqueId())) {
                        ProjectileData projectileData = new ProjectileData(newProjectile, data.getDamage(), 20);
                        projectileData.setSummonsLightning(data.summonsLightning());
                        projectileData.shouldTeleport(data.shouldTeleport());

                        projectileData.setShouldBounce(true);
                        plugin.projectiles.put(newProjectile.getUniqueId(), projectileData);
                    }
                }
            }
        }
    }
}
