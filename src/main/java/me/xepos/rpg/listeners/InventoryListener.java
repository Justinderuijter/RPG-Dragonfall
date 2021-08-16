package me.xepos.rpg.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.xepos.rpg.PlayerManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.database.DatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.datatypes.TreeData;
import me.xepos.rpg.utils.PacketUtils;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public class InventoryListener implements Listener {

    private final XRPG plugin;
    private final PlayerManager playerManager;
    private final DatabaseManager databaseManager;
    private final SkillLoader skillLoader;

    public InventoryListener(XRPG plugin, SkillLoader skillLoader, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        this.databaseManager = databaseManager;
        this.skillLoader = skillLoader;
    }

    @EventHandler
    public void onItemClick(final InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(player, true);

        if (xrpgPlayer != null && xrpgPlayer.isSpellCastModeEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> PacketUtils.sendSpellmodePacket(xrpgPlayer), 1);
            if (e.getSlot() >= 0 && e.getSlot() < xrpgPlayer.getSpellKeybinds().size() && e.getClickedInventory() instanceof PlayerInventory) {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getView().getTitle().equalsIgnoreCase("Spellbook")) {
            if (xrpgPlayer == null || e.getClick() == ClickType.NUMBER_KEY){
                e.setCancelled(true);
                return;
            }

            if (!(e.getClickedInventory() instanceof PlayerInventory)) {
                if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
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
                        if (e.getCursor().getType() != Material.AIR){
                            e.getWhoClicked().sendMessage(ChatColor.RED + "You cannot save while holding a skill!");
                            return;
                        }

                        updateKeybinds(xrpgPlayer, e.getClickedInventory());

                        e.getWhoClicked().closeInventory();
                        new SavePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);
                    }
                }
                return;

            }
            e.setCancelled(true);
        } else if (e.getView().getTitle().equals("Change Your Class") || e.getView().getTitle().equals("Pick A Class")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("classId"), PersistentDataType.STRING)){
                if (xrpgPlayer == null) return;

                String classId = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("classId"), PersistentDataType.STRING);
                if (classId == null) return;

                plugin.getClassChangeManager().changeClass(xrpgPlayer, classId, false);
            }
            
        } else if (e.getView().getTitle().startsWith("Skill Tree: ")) {
            e.setCancelled(true);
            if (xrpgPlayer == null) return;

            TreeData data = plugin.getTreeView(player.getUniqueId());
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
                if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("skillId"), PersistentDataType.STRING)) {
                    final String skillId = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("skillId"), PersistentDataType.STRING);

                    if (e.getClick() == ClickType.LEFT) {
                        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("level"), PersistentDataType.INTEGER)) {
                            int level = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("level"), PersistentDataType.INTEGER);
                            int maxLevel = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("maxLevel"), PersistentDataType.INTEGER);

                            if (level == 0) {
                                if (xrpgPlayer.getSkillUnlockPoints() > 0 && data.hasUnlockPoints() && data.hasRequired(skillId, true, false)){

                                    data.addSkillLevel(skillId);
                                    //Update icon
                                    data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), 1);
                                }
                            } else {
                                if (xrpgPlayer.getSkillUpgradePoints() > 0 && data.hasUpgradePoints() && data.isNotMaxed(skillId)){
                                    data.addSkillLevel(skillId);

                                    //Update icon
                                    data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), data.getCurrentSkillLevel(skillId));
                                }
                            }
                        }else if(e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("attribute"), PersistentDataType.STRING)){
                            final String attributeId = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("attribute"), PersistentDataType.STRING);

                            data.addAttributeLevel(attributeId);
                        }
                    } else if (e.getClick() == ClickType.RIGHT) {
                        data.revertSkill(skillId, false);

                        data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), data.getCurrentSkillLevel(skillId));
                    }
                }else if(e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("attribute"), PersistentDataType.STRING)){
                    final String attributeId = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("attribute"), PersistentDataType.STRING);

                    if (e.getClick() == ClickType.LEFT){
                        data.addAttributeLevel(attributeId);
                        data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), data.getCurrentAttributeLevel(attributeId));
                    }else if (e.getClick() == ClickType.RIGHT){
                        data.removeAttributeLevel(attributeId);
                        data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), data.getCurrentAttributeLevel(attributeId));
                    }
                }else if(e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("separator"), PersistentDataType.BYTE) && e.getCurrentItem().getType() == Material.WRITABLE_BOOK){
                    // save logic
                    data.applyChanges(skillLoader);

                    new SavePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);

                    player.closeInventory();
                }

            }
        }
    }

    @EventHandler
    public void onItemDrag(final InventoryDragEvent e) {
        if (e.getView().getTitle().equals("Change Your Class") || e.getView().getTitle().equals("Pick A Class")) {
            e.setCancelled(true);
        } else if (e.getView().getTitle().equals("Spellbook")) {

            Set<Integer> bottom = e.getRawSlots();
            bottom.removeIf(x -> x < e.getView().getTopInventory().getSize());

            if (e.getRawSlots().stream().anyMatch(bottom::contains)) {
                e.setCancelled(true);
            }


        } else {
            XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getWhoClicked().getUniqueId(), true);
            if (xrpgPlayer == null) return;

            if (xrpgPlayer.isSpellCastModeEnabled()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> PacketUtils.sendSpellmodePacket(xrpgPlayer), 1);
            }
        }

    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("Spellbook")){
            e.getPlayer().setItemOnCursor(null);
        }

        //Check if we are actually allowed to discard the object
        if (e.getView().getTitle().startsWith("Skill Tree: ")){
            plugin.removeTreeViewer(e.getPlayer().getUniqueId());
        }

    }

    @EventHandler
    public void onItemSwap(final PlayerSwapHandItemsEvent e) {
        final XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer(), true);

        if (xrpgPlayer == null) return;

/*        if (!xrpgPlayer.isShieldAllowed() && e.getOffHandItem().getType() == Material.SHIELD) {
            e.setCancelled(true);
        } else */
        if (xrpgPlayer.isSpellCastModeEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent e) {
        if (e.getTargetEntity() instanceof Player) {
            XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getTargetEntity().getUniqueId());
            if (xrpgPlayer != null){
                if (xrpgPlayer.getPassiveHandlerList().containsKey("ARMOR_DISPENSE")){
                    xrpgPlayer.getPassiveEventHandler("ARMOR_DISPENSE").invoke(e);
                }
            }
        }
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent e){
        XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(e.getPlayer());
        if (xrpgPlayer != null && xrpgPlayer.getPassiveHandlerList().containsKey("ARMOR_CHANGE")){
            xrpgPlayer.getPassiveEventHandler("ARMOR_CHANGE").invoke(e);
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
