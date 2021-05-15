package me.xepos.rpg.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;


public class PlayerListener implements Listener {
    private final XRPG plugin;
    private final IDatabaseManager databaseManager;

    public PlayerListener(XRPG plugin, IDatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    //Giving other plugins more opportunity to cancel this event
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            if (event instanceof EntityDamageByEntityEvent){

                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
                    XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer((Player) e.getDamager());
                    if (xrpgPlayer.isStunned())
                        e.setCancelled(true);
                    else
                        xrpgPlayer.getEventHandler("DAMAGE_DEALT").invoke(e);
                }

                if (e.getEntity() instanceof Player) {
                    Player player = (Player) e.getEntity();
                    XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);

                    e.setDamage(e.getDamage() * xrpgPlayer.getDamageTakenMultiplier());

                    xrpgPlayer.getEventHandler("DAMAGE_TAKEN").invoke(e);

                }

            }else{
                Player player = (Player) event.getEntity();
                XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);

                xrpgPlayer.getEventHandler("DAMAGE_TAKEN_ENVIRONMENTAL").invoke(event);
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

        if (plugin.getRPGPlayers().containsKey(player.getUniqueId())) {
            xrpgPlayer = plugin.getXRPGPlayer(player);
            xrpgPlayer.setPlayer(player);
        }

        if (xrpgPlayer != null && xrpgPlayer.getPlayer() != null) {
            //Adds modifiers specified in the class config
            addClassModifiers(xrpgPlayer);
            player.sendMessage("You are now " + xrpgPlayer.getClassDisplayName());

        } else {
            player.kickPlayer("Something went wrong while loading XRPG data.");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Utils.removeAllModifiers(player);
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);
        new SavePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);
        plugin.removeXRPGPlayer(player);
    }


    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        plugin.getXRPGPlayer(player).getEventHandler("CONSUME_ITEM");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = e.getPlayer();
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            ItemStack item = e.getItem();
            //Cancel using shield if not allowed
            if (item != null && item.getType() == Material.SHIELD && !xrpgPlayer.isShieldAllowed()) {
                player.sendMessage(ChatColor.RED + "You can't use shields!");
                player.sendMessage(ChatColor.RED + "Attempting to use it slowed you down!");
                player.sendMessage(ChatColor.RED + "Yet you don't seem to be blocking anything at all...");
                e.setCancelled(true);
                return;
            }

            if (e.getPlayer().isSneaking()) {
                xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK").invoke(e);
            } else {
                xrpgPlayer.getEventHandler("RIGHT_CLICK").invoke(e);
            }

        } else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

            doBowCycle(e.getItem(), xrpgPlayer);

            if (e.getPlayer().isSneaking()) {
                xrpgPlayer.getEventHandler("SNEAK_LEFT_CLICK").invoke(e);
            } else {
                xrpgPlayer.getEventHandler("LEFT_CLICK").invoke(e);
            }
        }

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer());
        if (e.getPlayer().isSneaking()) {
            xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK_ENTITY").invoke(e);
        } else {
            xrpgPlayer.getEventHandler("RIGHT_CLICK_ENTITY").invoke(e);
        }
    }

    public void onHealthRegen(EntityRegainHealthEvent e){
        if (!(e.getEntity() instanceof Player)) return;
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getEntity().getUniqueId());
        if (xrpgPlayer != null){
            xrpgPlayer.getEventHandler("HEALTH_REGEN").invoke(e);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            PlayerInventory inventory = player.getInventory();
            if (inventory.getItemInMainHand().getItemMeta() != null && inventory.getItemInMainHand().getItemMeta().getPersistentDataContainer().has(plugin.getTagKey(), PersistentDataType.STRING)
                    || (inventory.getItemInOffHand().getItemMeta() != null && inventory.getItemInOffHand().getItemMeta().getPersistentDataContainer().has(plugin.getTagKey(), PersistentDataType.STRING))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getEntity().getUniqueId());
            if (!plugin.getFileConfiguration(xrpgPlayer.getClassId()).getBoolean("allow-bow", true)) {
                xrpgPlayer.getPlayer().sendMessage(ChatColor.RED + "You're not lectured on archery!");
                e.setConsumeItem(false);
                e.setCancelled(true);
                return;
            }
            xrpgPlayer.getEventHandler("SHOOT_BOW").invoke(e);
        }
    }

    @EventHandler
    public void onToggleSprint(PlayerToggleSprintEvent e){
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null){
            xrpgPlayer.getEventHandler("SPRINT").invoke(e);
        }
    }

    @EventHandler
    public void onSwapHeldItem(PlayerItemHeldEvent e){
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null && xrpgPlayer.isSpellCastModeEnabled()){
            xrpgPlayer.getEventHandler("SWAP_HELD_ITEM").invoke(e);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleSprint(PlayerJumpEvent e){
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null){
            xrpgPlayer.getEventHandler("JUMP").invoke(e);
        }
    }

    private void doBowCycle(ItemStack item, XRPGPlayer xrpgPlayer) {
        if (item != null && item.getType() == Material.BOW) {
            xrpgPlayer.getEventHandler("SHOOT_BOW").next();
        }
    }

    private void addClassModifiers(XRPGPlayer xrpgPlayer){
        AttributeModifierManager manager = AttributeModifierManager.getInstance();
        for (String id: manager.getModifiers(ModifierType.POSITIVE).keySet()) {
            if (id.startsWith(xrpgPlayer.getClassId().toUpperCase())){
                Utils.addUniqueModifier(xrpgPlayer.getPlayer(), manager.get(ModifierType.POSITIVE, id));
                xrpgPlayer.getPlayer().sendMessage("Added " + id);
            }
        }
    }
}
