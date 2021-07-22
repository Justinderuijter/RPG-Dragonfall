package me.xepos.rpg.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.PlayerManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.DatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.utils.PacketUtils;
import me.xepos.rpg.utils.SpellmodeUtils;
import me.xepos.rpg.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
/*                        if(e.getCause() != EntityDamageEvent.DamageCause.CUSTOM){
                            e.setDamage(DamageUtils.calculateDamage(e.getDamage(), xrpgPlayer.getLevel(), livingEntity));
                        }*/
                        xrpgPlayer.getPassiveEventHandler("DAMAGE_DEALT").invoke(e);
                    }
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
            if(event.getEntity() instanceof Player player) {
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
            //Cancel using shield if not allowed
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

    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent e){
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer(), true);
        if (xrpgPlayer != null && xrpgPlayer.isSpellCastModeEnabled() && e.getNewGameMode() != GameMode.SURVIVAL) {
            SpellmodeUtils.disableSpellmode(xrpgPlayer);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onXRPGSpellCast(XRPGSpellCastEvent e){
        XRPGPlayer xrpgPlayer = e.getSkill().getXRPGPlayer();
        PassiveEventHandler passiveEventHandler = xrpgPlayer.getPassiveEventHandler("XRPG_SPELL_CAST");
        if (passiveEventHandler != null) {
            passiveEventHandler.invoke(e);
        }
    }
}
