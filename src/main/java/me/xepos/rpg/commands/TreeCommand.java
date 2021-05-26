package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.TreeLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class TreeCommand implements CommandExecutor {
    private final XRPG plugin;
    private final TreeLoader treeLoader;
    private final Inventory inventory;

    public TreeCommand(XRPG plugin, TreeLoader treeLoader, Inventory inventory){
        this.plugin = plugin;
        this.treeLoader = treeLoader;
        this.inventory = inventory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equals("tree")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("This command can only be executed by players!");
                return true;
            }
            Player player = (Player) commandSender;
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player);
            if (xrpgPlayer == null){
                player.sendMessage(ChatColor.RED + "Cannot use this command!");
                return true;
            }

            player.openInventory(inventory);
            return true;
        }



        return false;
    }
}
