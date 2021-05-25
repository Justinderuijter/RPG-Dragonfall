package me.xepos.rpg.utils;

import me.xepos.rpg.XRPGPlayer;
import org.bukkit.inventory.PlayerInventory;

public final class SpellmodeUtils {

    public static void enterSpellmode(XRPGPlayer xrpgPlayer){
        xrpgPlayer.setSpellCastModeEnabled(true);
        PlayerInventory inventory = xrpgPlayer.getPlayer().getInventory();

        int keybindSize = xrpgPlayer.getSpellKeybinds().size();
        if(inventory.getHeldItemSlot() < keybindSize)
            inventory.setHeldItemSlot(keybindSize + 1);

        PacketUtils.sendSpellmodePacket(xrpgPlayer);
    }

    public static void disableSpellmode(XRPGPlayer xrpgPlayer){
        xrpgPlayer.setSpellCastModeEnabled(false);
        xrpgPlayer.getPlayer().updateInventory();
    }
}
