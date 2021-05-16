package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToggleSpellCommand implements TabExecutor {
    private final XRPG plugin;
    private final List<String> completions = new ArrayList<String>(){{
        add("on");
        add("off");
        add("enable");
        add("disable");
        add("toggle");
        add("status");
    }};

    public ToggleSpellCommand(XRPG plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("spellmode") || command.getName().equalsIgnoreCase("sm")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("This command can only be execute by players!");
                return true;
            }
            if (strings.length > 1) return false;

            UUID senderUUID = ((Player) commandSender).getUniqueId();
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(senderUUID);
            if (xrpgPlayer == null) {
                return true;
            }

            switch (strings[0].toLowerCase()) {
                case "on":
                case "enable":
                    xrpgPlayer.setSpellCastModeEnabled(true);
                    PlayerInventory inventory = xrpgPlayer.getPlayer().getInventory();
                    swapItems(inventory, 7);
                    inventory.setHeldItemSlot(7);
                    return true;
                case "off":
                case "disable":
                    xrpgPlayer.setSpellCastModeEnabled(false);
                    return true;
                case "toggle":
                    xrpgPlayer.setSpellCastModeEnabled(!xrpgPlayer.isSpellCastModeEnabled());
                    return true;
                default:
                    commandSender.sendMessage("Spellcast mode is " + (xrpgPlayer.isSpellCastModeEnabled() ? "enabled." : "disabled."));
                    return true;
            }
        }
        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();
        if (strings.length == 1){
            for (String input:strings) {
                if (input.toLowerCase().startsWith(strings[0].toLowerCase())){
                    result.add(input);
                }
            }
            return result;
        }
        return null;
    }

    private void swapItems(final PlayerInventory playerInventory, final int targetSlot){
        final int heldItemSlot = playerInventory.getHeldItemSlot();
        ItemStack heldItem = playerInventory.getItem(heldItemSlot);

        ItemStack targetItem = playerInventory.getItem(targetSlot);

        playerInventory.setItem(targetSlot, heldItem);
        playerInventory.setItem(heldItemSlot, targetItem);
    }
}
