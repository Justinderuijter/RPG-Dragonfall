package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.ClassLoader;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.events.XRPGClassChangedEvent;
import me.xepos.rpg.utils.PacketUtils;
import me.xepos.rpg.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

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
        Player player = (Player) e.getWhoClicked();
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);

        if (xrpgPlayer.isSpellCastModeEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> PacketUtils.testingPacket(xrpgPlayer), 1);
            if (e.getSlot() < xrpgPlayer.getSpellKeybinds().size() && e.getClickedInventory() instanceof PlayerInventory) {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getView().getTitle().equalsIgnoreCase("Pick A Class")) {
            if (e.getCurrentItem() == null)
                return;

            if (xrpgPlayer == null) {
                e.setCancelled(true);
                return;
            }

            ItemMeta meta = e.getCurrentItem().getItemMeta();
            if (meta != null) {
                String classId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "classId"), PersistentDataType.STRING);
                String classDisplayName = e.getCurrentItem().getItemMeta().getDisplayName();

                if (classId == null) {
                    e.setCancelled(true);
                    return;
                }

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
        } else if (e.getView().getTitle().equalsIgnoreCase("Spellbook")) {
            if (!(e.getClickedInventory() instanceof PlayerInventory)) {
                e.getWhoClicked().sendMessage("Spellbook: " + e.getSlot());

                if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){
                    e.setCancelled(true);
                    return;
                }

                if (e.getCursor() != null && e.getCursor().getItemMeta() != null && e.getCursor().getItemMeta().getPersistentDataContainer().has(plugin.getKey("skillId"), PersistentDataType.STRING)
                        && e.getClickedInventory() == null) {
                    e.setCancelled(true);
                    return;
                }


                //Check if player clicked a separator
                //If it's the book and quill they clicked the save button.
                if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("separator"), PersistentDataType.BYTE)) {
                    e.setCancelled(true);
                    if (e.getCurrentItem().getType() == Material.WRITABLE_BOOK) {
                        e.getWhoClicked().sendMessage(Component.text("You clicked save"));

                        updateKeybinds(xrpgPlayer, e.getClickedInventory());

                        e.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        new SavePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);
                    }
                }
                return;

            }
            e.setCancelled(true);
        } else /*if(e.getInventory() instanceof PlayerInventory)*/ {
            if (xrpgPlayer == null) return;

            if (e.getSlot() == 40 && e.getCursor().getType() == Material.SHIELD && !xrpgPlayer.isShieldAllowed()) {
                e.getWhoClicked().sendMessage("Can't use shield");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrag(final InventoryDragEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("Pick A Class")) {
            e.setCancelled(true);
        }else if (e.getView().getTitle().equals("Spellbook")){

            Set<Integer> bottom = e.getRawSlots();
            bottom.removeIf(x -> x < e.getView().getTopInventory().getSize());

            if (e.getRawSlots().stream().anyMatch(bottom::contains)){
                e.setCancelled(true);
            }


        } else {
            if (e.getInventorySlots().contains(40)) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals("Spellbook")) return;

        e.getPlayer().setItemOnCursor(null);
    }

    @EventHandler
    public void onItemSwap(final PlayerSwapHandItemsEvent e) {
        final XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer());

        if (xrpgPlayer == null) return;

        if (!xrpgPlayer.isShieldAllowed() && e.getOffHandItem().getType() == Material.SHIELD) {
            e.setCancelled(true);
        } else if (xrpgPlayer.isSpellCastModeEnabled()) {
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

    private void updateKeybinds(XRPGPlayer xrpgPlayer, Inventory inventory) {
        xrpgPlayer.getSpellKeybinds().clear();
        final int startIndex = inventory.getSize() - 9;
        for (int i = startIndex; i < startIndex + 7; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;

            final NamespacedKey key = plugin.getKey("skillId");
            if (item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String skillId = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);

                if (skillId == null) continue;

                xrpgPlayer.getSpellKeybinds().add(skillId);
            }

        }
    }

}
