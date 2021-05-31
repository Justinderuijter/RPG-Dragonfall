package me.xepos.rpg.utils;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpellmodeUtils {

    public static void enterSpellmode(XRPGPlayer xrpgPlayer){
        xrpgPlayer.setSpellCastModeEnabled(true);
        PlayerInventory inventory = xrpgPlayer.getPlayer().getInventory();

        moveSpellbookIfPresent(inventory);

        int keybindSize = xrpgPlayer.getSpellKeybinds().size();
        if(inventory.getHeldItemSlot() < keybindSize)
            inventory.setHeldItemSlot(keybindSize + 1);

        PacketUtils.sendSpellmodePacket(xrpgPlayer);
    }

    public static void disableSpellmode(XRPGPlayer xrpgPlayer){
        xrpgPlayer.setSpellCastModeEnabled(false);
        xrpgPlayer.getPlayer().updateInventory();
    }
    
    private static void moveSpellbookIfPresent(PlayerInventory playerInventory){
        XRPG plugin = JavaPlugin.getPlugin(XRPG.class);
        Integer slot = null;

        ItemStack itemAtSlot8 = playerInventory.getItem(8);

        if (itemAtSlot8 == null || itemAtSlot8.getItemMeta() == null || !itemAtSlot8.getItemMeta().getPersistentDataContainer().has(plugin.getKey("spellbook"), PersistentDataType.BYTE)){
            for (int i = 0; i < playerInventory.getSize(); i++) {
                ItemStack item = playerInventory.getItem(i);
                if (item != null && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(plugin.getKey("spellbook"), PersistentDataType.BYTE)){
                    slot = i;
                    break;
                }
            }

            if (slot != null){
                ItemStack spellbook = playerInventory.getItem(slot);
                playerInventory.setItem(slot, playerInventory.getItem(8));
                playerInventory.setItem(8, spellbook);
            }
        }
    }
}
