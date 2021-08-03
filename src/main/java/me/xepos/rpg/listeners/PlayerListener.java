package me.xepos.rpg.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.PlayerManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.DatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.datatypes.BaseProjectileData;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.utils.DamageUtils;
import me.xepos.rpg.utils.PacketUtils;
import me.xepos.rpg.utils.SpellmodeUtils;
import me.xepos.rpg.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;


public class PlayerListener implements Listener {
    private final XRPG plugin;
    private final PlayerManager playerManager;
    private final DatabaseManager databaseManager;

    public PlayerListener(XRPG plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        this.databaseManager = databaseManager;
    }

    //Giving other plugins more opportunity to cancel this event
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity livingEntity) {
                XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer((Player) e.getDamager(), true);
                if (xrpgPlayer != null) {
                    if (xrpgPlayer.isStunned())
                        e.setCancelled(true);
                    else if (xrpgPlayer.isClassEnabled()) {
                        xrpgPlayer.getPassiveEventHandler("DAMAGE_DEALT").invoke(e);
                    }
                }
            } else if (e.getDamager() instanceof Projectile projectile) {
                //This block deals with projectile handling
                BaseProjectileData baseProjectileData = plugin.projectiles.get(projectile.getUniqueId());
                if (baseProjectileData != null) {

                    if (baseProjectileData.summonsLightning()) {
                        e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation());
                    }

                    if (baseProjectileData.shouldTeleport()) {
                        baseProjectileData.getShooter().teleport(e.getEntity(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }

                    if (baseProjectileData instanceof ProjectileData projectileData) {
                        projectileData.summonCloud();
                        e.getEntity().setFireTicks(projectileData.getFireTicks());

                        if (e.getEntity() instanceof LivingEntity livingEntity) {
                            projectileBounceLogic(e, projectileData);
                            //Section exclusively for projectiles that aren't arrows
                            if (!(e.getDamager() instanceof Arrow)) {
                                livingEntity.setNoDamageTicks(0);
                                final double damage = DamageUtils.calculateSpellDamage(projectileData.getDamage(), projectileData.getShooterLevel(), livingEntity);
                                if (damage <= 0){
                                    e.setCancelled(true);
                                }else{
                                    e.setDamage(damage);
                                }
                                plugin.projectiles.remove(projectileData.getProjectile().getUniqueId());
                                return;
                            }

                            //section exclusively for arrows.
                            boolean damageBoosted = false;

                            if (projectileData.getDamageMultiplier() < 1.0) {
                                final double multiplier = DamageUtils.getSpellDamageMultiplier(baseProjectileData.getShooterLevel(), livingEntity);
                                final double damage = (livingEntity.getHealth() - livingEntity.getHealth() * projectileData.getDamageMultiplier()) * multiplier;
                                Utils.decreaseHealth(livingEntity, damage);
                                projectile.remove();
                                e.setCancelled(true);
                            }

                            if (projectileData.getDamage() != 0) {
                                Utils.decreaseHealth(livingEntity, DamageUtils.calculateSpellDamage(projectileData.getDamage(), projectileData.getShooterLevel(), livingEntity));
                                livingEntity.setNoDamageTicks(1);
                                projectile.remove();
                                e.setCancelled(true);
                            }

                            if (projectileData.getHeadshotDamage() != 1.0) {
                                if (projectile.getLocation().getY() - livingEntity.getLocation().getY() > getHeadShotHeight(livingEntity)) {
                                    Arrow arrow = (Arrow) projectile;
                                    if (damageBoosted){
                                        e.setDamage(e.getDamage() * projectileData.getHeadshotDamage());
                                    }else{
                                        e.setDamage(projectileData.getHeadshotDamage() * DamageUtils.calculateDamage(e.getDamage(), baseProjectileData.getShooterLevel(), livingEntity));
                                        damageBoosted = true;
                                    }


                                    ((LivingEntity) arrow.getShooter()).sendMessage(ChatColor.DARK_GREEN + "You headshot " + livingEntity.getName() + "!");
                                }
                            }

                            if (projectileData.shouldDisengage()) {
                                LivingEntity shooter = (LivingEntity) projectile.getShooter();
                                Vector unitVector = shooter.getLocation().toVector().subtract(livingEntity.getLocation().toVector()).normalize();
                                if (!damageBoosted){
                                    e.setDamage(DamageUtils.calculateSpellDamage(e.getDamage(), baseProjectileData.getShooterLevel(), livingEntity));
                                }

                                shooter.setVelocity(unitVector.multiply(1.5));
                            }
                        }
                    }else if(baseProjectileData instanceof ExplosiveProjectileData explosiveProjectileData){
                        Location location = e.getEntity().getLocation();

                        location.getWorld().createExplosion(location, explosiveProjectileData.getYield(), explosiveProjectileData.setsFire(), explosiveProjectileData.destroysBlocks(), explosiveProjectileData.getShooter());

                        if (explosiveProjectileData.getProjectile() instanceof Arrow) {
                            explosiveProjectileData.getProjectile().remove();
                        }
                    }
                    plugin.projectiles.remove(baseProjectileData.getProjectile().getUniqueId());
                }
            }

            if (e.getEntity() instanceof Player player) {
                XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(player, true);
                if (xrpgPlayer != null) {
                    e.setDamage(e.getDamage() * xrpgPlayer.getDamageTakenMultiplier());

                    xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").invoke(e);
                }

            }

        } else {
            if (event.getEntity() instanceof Player player) {
                XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(player);

                if (xrpgPlayer != null) {
                    xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN_ENVIRONMENTAL").invoke(event);
                }
            }
        }

    }

    @EventHandler
    public void onPrePlayerJoin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.databaseManager.loadPlayerData(e.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        XRPGPlayer xrpgPlayer = null;

        if (this.plugin.getConfig().getBoolean("safety-options.modifier-check-on-join", true))
            AttributeModifierManager.getInstance().removeAllXRPGModifiers(player);

        if (playerManager.getXRPGPlayers().containsKey(player.getUniqueId())) {
            xrpgPlayer = playerManager.getXRPGPlayer(player, true);
            if (xrpgPlayer != null) {
                xrpgPlayer.setPlayer(player);
            }
        }

        if (xrpgPlayer != null && xrpgPlayer.getPlayer() != null) {
            xrpgPlayer.addQueuedModifiers();
            if (!StringUtils.isEmpty(xrpgPlayer.getClassId())) {
                player.sendMessage("You are now " + xrpgPlayer.getClassId());
            }
            playerManager.consumeLoginConsumers(player.getUniqueId());
        } else {
            playerManager.remove(player.getUniqueId());
            player.kickPlayer("Something went wrong while loading XRPG data.");
        }

        if (!player.hasPlayedBefore()) {
            player.getInventory().addItem(plugin.getSpellbookItem());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Utils.removeAllModifiers(player);
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(player, true);
        xrpgPlayer.clearAllPermanentPotionEffects();
        new SavePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);
        playerManager.remove(player);
    }


    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(player);
        if (xrpgPlayer != null) {
            xrpgPlayer.getPassiveEventHandler("CONSUME_ITEM");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = e.getPlayer();
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(player);

        if (xrpgPlayer == null) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            ItemStack item = e.getItem();
            if (item != null) {

                if (item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(plugin.getKey("spellbook"), PersistentDataType.BYTE)) {
                    if (xrpgPlayer.isSpellCastModeEnabled()) {
                        SpellmodeUtils.disableSpellmode(xrpgPlayer);
                    } else {
                        SpellmodeUtils.enterSpellmode(xrpgPlayer);
                    }
                    return;

                }
            }

            //TODO: These might actually be ancient and need removal, needs checking
            if (e.getPlayer().isSneaking()) {
                xrpgPlayer.getPassiveEventHandler("SNEAK_RIGHT_CLICK").invoke(e);
            } else {
                xrpgPlayer.getPassiveEventHandler("RIGHT_CLICK").invoke(e);
            }

        } else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (e.getPlayer().isSneaking()) {
                xrpgPlayer.getPassiveEventHandler("SNEAK_LEFT_CLICK").invoke(e);
            } else {
                xrpgPlayer.getPassiveEventHandler("LEFT_CLICK").invoke(e);
            }
        }

        if (xrpgPlayer.isSpellCastModeEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> PacketUtils.sendSpellmodePacket(xrpgPlayer), 1);
            if (player.getInventory().getHeldItemSlot() < xrpgPlayer.getSpellKeybinds().size()) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHealthRegen(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getEntity().getUniqueId());
        if (xrpgPlayer != null) {
            xrpgPlayer.getPassiveEventHandler("HEALTH_REGEN").invoke(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getEntity().getUniqueId());
            if (xrpgPlayer != null) {
                xrpgPlayer.getPassiveEventHandler("SHOOT_BOW").invoke(e);
            }
        }
    }

    @EventHandler
    public void onToggleSprint(PlayerToggleSprintEvent e) {
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null) {
            xrpgPlayer.getPassiveEventHandler("SPRINT").invoke(e);
        }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null) {
            xrpgPlayer.getPassiveEventHandler("SNEAK").invoke(e);
        }
    }

    @EventHandler
    public void onSwapHeldItem(PlayerItemHeldEvent e) {
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer());

        if (xrpgPlayer != null) {
            if (xrpgPlayer.isSpellCastModeEnabled()) {
                if (e.getNewSlot() < xrpgPlayer.getSpellKeybinds().size()) {
                    xrpgPlayer.getActiveHandler().invoke(e);
                    e.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent e) {
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null) {
            xrpgPlayer.getPassiveEventHandler("JUMP").invoke(e);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null && xrpgPlayer.isSpellCastModeEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> PacketUtils.sendSpellmodePacket(xrpgPlayer), 1);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExpChange(PlayerExpChangeEvent e) {
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null) {
            if (xrpgPlayer.getPassiveHandlerList().containsKey("EXP_CHANGE")) {
                xrpgPlayer.getPassiveEventHandler("EXP_CHANGE").invoke(e);
            }
        }
    }

    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent e) {
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer(), true);
        if (xrpgPlayer != null && xrpgPlayer.isSpellCastModeEnabled() && e.getNewGameMode() != GameMode.SURVIVAL) {
            SpellmodeUtils.disableSpellmode(xrpgPlayer);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onXRPGSpellCast(XRPGSpellCastEvent e) {
        XRPGPlayer xrpgPlayer = e.getSkill().getXRPGPlayer();
        PassiveEventHandler passiveEventHandler = xrpgPlayer.getPassiveEventHandler("XRPG_SPELL_CAST");
        if (passiveEventHandler != null) {
            passiveEventHandler.invoke(e);
        }
    }

    private void projectileBounceLogic(EntityDamageByEntityEvent e, ProjectileData data) {

        //if (e.getHitBlock() != null) {
        //    plugin.projectiles.remove(data.getProjectile().getUniqueId());
        //    return;
        //}

        if (data.shouldBounce()) {
            if (e.getEntity() instanceof LivingEntity livingEntity) {

                livingEntity.damage(DamageUtils.calculateSpellDamage(data.getDamage(), data.getShooterLevel(), livingEntity), data.getShooter());
                LivingEntity newTarget = Utils.getRandomLivingEntity(livingEntity, 20.0, 4.0, data.getShooter(), true);
                if (newTarget != null) {
                    Vector vector = newTarget.getLocation().toVector().subtract(livingEntity.getLocation().toVector());
                    Projectile newProjectile = livingEntity.launchProjectile(data.getProjectile().getClass(), vector.normalize());
                    newProjectile.setShooter(data.getProjectile().getShooter());

                    if (!plugin.projectiles.containsKey(newProjectile.getUniqueId())) {
                        ProjectileData projectileData = new ProjectileData(newProjectile, data.getShooterLevel(), data.getDamage(), 20);
                        projectileData.setSummonsLightning(data.summonsLightning());
                        projectileData.shouldTeleport(data.shouldTeleport());

                        projectileData.setShouldBounce(true);
                        plugin.projectiles.put(newProjectile.getUniqueId(), projectileData);
                    }
                }
            }
        }
    }

    private double getHeadShotHeight(LivingEntity livingEntity) {
        if (livingEntity instanceof Player) {
            if (((Player) livingEntity).isSneaking()) {
                return 1.1D;
            }
            return 1.4D;
        } else if (livingEntity instanceof Giant) return 9.0D;
        else if (livingEntity instanceof IronGolem || livingEntity instanceof WitherSkeleton) return 2.0D;
            //For these mobs the entire body is considered the head
        else if (livingEntity instanceof Slime || livingEntity instanceof Ghast || livingEntity instanceof Guardian)
            return -1.0D;

        else if (livingEntity instanceof Ageable) {
            if (!((Ageable) livingEntity).isAdult()) return 0.6D;
        }

        return 1.4D;
    }
}
