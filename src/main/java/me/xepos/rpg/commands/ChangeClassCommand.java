package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChangeClassCommand implements CommandExecutor {
    private final XRPG plugin;
    private final Inventory inventory;

    public ChangeClassCommand(XRPG plugin, Inventory inventory)
    {
        this.plugin = plugin;
        this.inventory = inventory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("changeclass") ||command.getName().equals("cs"))
        {
            if (commandSender instanceof Player){
                Player player = (Player)commandSender;
                Inventory gui = inventory;
                gui.setItem(0, Utils.buildItemStack(Material.PAPER, "Free Change Ticket", new ArrayList<String>() {{
                    add("Owned amount: " + plugin.getXRPGPlayer((Player) commandSender).getFreeChangeTickets());
                }}));
                player.openInventory(gui);
                return true;
            }

        }
        return false;
    }
}
