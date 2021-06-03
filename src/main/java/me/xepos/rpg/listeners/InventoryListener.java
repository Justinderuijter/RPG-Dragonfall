package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.datatypes.TreeData;
import me.xepos.rpg.events.XRPGClassChangedEvent;
import me.xepos.rpg.utils.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
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
    private final IDatabaseManager databaseManager;
    private final SkillLoader skillLoader;

    public InventoryListener(XRPG plugin, SkillLoader skillLoader, IDatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.skillLoader = skillLoader;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player, true);

        if (xrpgPlayer != null && xrpgPlayer.isSpellCastModeEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> PacketUtils.sendSpellmodePacket(xrpgPlayer), 1);
            if (e.getSlot() < xrpgPlayer.getSpellKeybinds().size() && e.getClickedInventory() instanceof PlayerInventory) {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getView().getTitle().equalsIgnoreCase("Spellbook")) {
            if (xrpgPlayer == null){
                e.setCancelled(true);
                return;
            }

            if (!(e.getClickedInventory() instanceof PlayerInventory)) {
                e.getWhoClicked().sendMessage("Spellbook: " + e.getSlot());

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
                        //e.getWhoClicked().sendMessage(Component.text("You clicked save"));

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

                String classDisplayName = plugin.getClassInfo(classId).getDisplayName();
                if (classDisplayName == null) return;

                if (classId.equals(xrpgPlayer.getClassId())){
                    player.sendMessage(ChatColor.RED + "You already are " + xrpgPlayer.getClassDisplayName());
                    return;
                }

                XRPGClassChangedEvent event = new XRPGClassChangedEvent(player, xrpgPlayer.getClassId(), xrpgPlayer.getClassDisplayName(), classId, classDisplayName);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    //PlayerData data = xrpgPlayer.extractData();
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
                        PlayerData data = databaseManager.savePlayerData(xrpgPlayer);



                        data.setClassId(classId);

                        skillLoader.loadPlayerSkills(data, xrpgPlayer);
                    });

                    player.closeInventory();
                }
            }
            
        } else if (e.getView().getTitle().startsWith("Skill Tree: ")) {
            e.setCancelled(true);
            if (xrpgPlayer == null) return;

            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
                if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("skillId"), PersistentDataType.STRING)) {
                    final String skillId = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("skillId"), PersistentDataType.STRING);
                    //Set<String> learnedSkills = xrpgPlayer.getAllLearnedSkills().keySet();
                    TreeData data = plugin.getTreeView(player.getUniqueId());
                    //Backing out of the inventory will remove the object from the HashMap
                    if (e.getClick() == ClickType.LEFT) {
                        if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("level"), PersistentDataType.INTEGER)) {
                            int level = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("level"), PersistentDataType.INTEGER);
                            int maxLevel = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getKey("maxLevel"), PersistentDataType.INTEGER);

                            if (level == 0) {
                                if (xrpgPlayer.getSkillUnlockPoints() > 0 && data.hasUnlockPoints() && data.hasRequired(skillId, true)){

                                    data.addLevel(skillId);
                                    //Update icon
                                    data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), 1);
                                }
                            } else {
                                if (xrpgPlayer.getSkillUpgradePoints() > 0 && data.hasUpgradePoints() && data.isNotMaxed(skillId)){
                                    data.addLevel(skillId);

                                    //Update icon
                                    data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), data.getCurrentSkillLevel(skillId));
                                }
                            }
                        }
                    } else if (e.getClick() == ClickType.RIGHT) {
                        data.revertSkill(skillId, false);
                        /*SkillRefundType refundType = data.revertSkill(skillId);
                        if (refundType == SkillRefundType.REFUND_UNLOCK_POINT){
                            xrpgPlayer.setSkillUnlockPoints(xrpgPlayer.getSkillUnlockPoints() + 1);
                        }else if(refundType == SkillRefundType.REFUND_UPGRADE_POINT){
                            xrpgPlayer.setSkillUpgradePoints(xrpgPlayer.getSkillUpgradePoints() + 1);
                        }*/
                        data.updateClickedIcon(e.getClickedInventory(), e.getSlot(), e.getCurrentItem(), data.getCurrentSkillLevel(skillId));
                    }
                }else if(e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(plugin.getKey("separator"), PersistentDataType.BYTE) && e.getCurrentItem().getType() == Material.WRITABLE_BOOK){
                    // save logic
                    TreeData data = plugin.getTreeView(player.getUniqueId());
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
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getWhoClicked().getUniqueId(), true);
            if (xrpgPlayer == null) return;

            if (xrpgPlayer.isSpellCastModeEnabled()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> PacketUtils.sendSpellmodePacket(xrpgPlayer), 1);
            }
        }

    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals("Spellbook")){
            e.getPlayer().setItemOnCursor(null);
        }

        //Check if we are actually allowed to discard the object
        if (e.getView().getTitle().startsWith("Skill Tree: ")){
            plugin.removeTreeViewer(e.getPlayer().getUniqueId());
        }

    }

    @EventHandler
    public void onItemSwap(final PlayerSwapHandItemsEvent e) {
        final XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getPlayer(), true);

        if (xrpgPlayer == null) return;

/*        if (!xrpgPlayer.isShieldAllowed() && e.getOffHandItem().getType() == Material.SHIELD) {
            e.setCancelled(true);
        } else */
        if (xrpgPlayer.isSpellCastModeEnabled()) {
            e.setCancelled(true);
        }
    }

/*    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent e) {
        if (e.getTargetEntity() instanceof Player && e.getItem().getType() == Material.SHIELD) {
            if (!plugin.getXRPGPlayer(e.getTargetEntity().getUniqueId()).isShieldAllowed()) {
                e.setCancelled(true);
            }
        }
    }*/
    @EventHandler
    public void onPrepareEnchant(final PrepareItemEnchantEvent e){
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(e.getEnchanter());
        if (xrpgPlayer != null){
            if (xrpgPlayer.getPassiveHandlerList().containsKey("ENCHANT")){
                xrpgPlayer.getPassiveEventHandler("ENCHANT").invoke(e);
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
