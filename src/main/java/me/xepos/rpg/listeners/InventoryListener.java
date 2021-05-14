package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.ClassLoader;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.events.XRPGClassChangedEvent;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener {

    private final XRPG plugin;
    private final ClassLoader classLoader;
    private final IDatabaseManager databaseManager;

    public InventoryListener(XRPG plugin, ClassLoader classLoader, IDatabaseManager databaseManager) {
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Pick A Class")) {
            if (e.getCurrentItem() == null)
                return;

            ItemMeta meta = e.getCurrentItem().getItemMeta();
            if (meta != null) {
                String classId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "classId"), PersistentDataType.STRING);
                String classDisplayName = e.getCurrentItem().getItemMeta().getDisplayName();

                if (classId == null) {
                    e.setCancelled(true);
                    return;
                }

                Player player = (Player) e.getWhoClicked();
                XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);

                if (xrpgPlayer.getFreeChangeTickets() <= 0) {
                    player.sendMessage(ChatColor.RED + "You don't have enough tickets!");
                    e.setCancelled(true);
                    return;
                }

                if (xrpgPlayer.getClassId().equals(classId)) {
                    player.sendMessage(ChatColor.RED + "Can't change to this class, it's already your current class!");
                    e.setCancelled(true);
                    return;
                }

                XRPGClassChangedEvent event = new XRPGClassChangedEvent(player, xrpgPlayer.getClassId(), xrpgPlayer.getClassDisplayName(), classId, classDisplayName);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    PlayerData data = xrpgPlayer.extractData();

                    //Need to save before changing class to prevent data loss
                    new SavePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);

                    Bukkit.broadcastMessage(xrpgPlayer.getPlayer().getName() + " changed their class from " + xrpgPlayer.getClassDisplayName() + " to " + classDisplayName + "!");
                    data.setClassId(classId);
                    classLoader.loadClass(data, xrpgPlayer);
                    Utils.removeAllModifiers(player);
                }
            }

            e.setCancelled(true);
        } else /*if(e.getInventory() instanceof PlayerInventory)*/ {
            if (e.getSlot() == 40 && e.getCursor().getType() == Material.SHIELD) {
                e.getWhoClicked().sendMessage("Can't use shield");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrag(final InventoryDragEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Pick A Class")) {
            e.setCancelled(true);
        } else {
            if (e.getInventorySlots().contains(40)) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onItemSwap(final PlayerSwapHandItemsEvent e) {
        if (!plugin.getXRPGPlayer(e.getPlayer()).isShieldAllowed() && e.getOffHandItem().getType() == Material.SHIELD) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent e) {
        if (e.getTargetEntity() instanceof Player && e.getItem().getType() == Material.SHIELD) {
            if (!plugin.getXRPGPlayer(e.getTargetEntity().getUniqueId()).isShieldAllowed()) {
                e.setCancelled(true);
            }
        }
    }
}
