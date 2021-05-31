package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChangeClassCommand implements TabExecutor {
    private final XRPG plugin;
    private final List<ItemStack> baseItems;

    private final List<String> completions = new ArrayList<String>() {{
        add("select");
        add("change");
        add("enable");
        add("disable");
        add("toggle");
    }};

    public ChangeClassCommand(XRPG plugin, List<ItemStack> itemStacks) {
        this.plugin = plugin;
        this.baseItems = itemStacks;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equals("class")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("This command can only be executed by players.");
                return true;
            }

            Player player = (Player) commandSender;
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player, true);
            if (xrpgPlayer == null) return true;

            final String enableMessage = ChatColor.GREEN + "Your class perks have been " + ChatColor.BOLD + "enabled!";
            final String disableMessage = ChatColor.GREEN + "Your class perks have been " + ChatColor.BOLD + "disabled!";

            if (strings.length == 1) {
                switch (strings[0].toLowerCase()) {
                    case "select":

                        if (StringUtils.isBlank(xrpgPlayer.getClassId())) {
                            //open class select GUI
                            Inventory inventory = Bukkit.createInventory(null, 9, "Pick A Class");

                            for (ItemStack baseItem : baseItems) {
                                inventory.addItem(baseItem);
                            }

                            player.openInventory(inventory);
                        } else {
                            player.sendMessage("You already have a class!");
                            player.sendMessage("Please use /class change");
                        }

                        return true;
                    case "change":
                        if (StringUtils.isNotBlank(xrpgPlayer.getClassId())) {
                            Inventory inventory = Bukkit.createInventory(null, 9, "Change Your Class");

                            for (ItemStack baseItem : baseItems) {
                                inventory.addItem(baseItem);
                            }
                            player.openInventory(inventory);
                        }else{
                            player.sendMessage("You do not have a class yet!");
                            player.sendMessage("Please use /class select");
                        }
                        return true;
                    case "enable":
                        if (xrpgPlayer.isClassEnabled()) {
                            player.sendMessage(ChatColor.GREEN + "Your class perks were already enabled!");
                            return true;
                        }
                        xrpgPlayer.setClassEnabled(true);
                        player.sendMessage(enableMessage);
                        return true;
                    case "disable":
                        if (!xrpgPlayer.isClassEnabled()) {
                            player.sendMessage(ChatColor.GREEN + "Your class perks were already disabled!");
                            return true;
                        }
                        xrpgPlayer.setClassEnabled(false);
                        player.sendMessage(disableMessage);
                        return true;
                    case "toggle":
                        if (xrpgPlayer.isClassEnabled()) {
                            xrpgPlayer.setClassEnabled(false);
                            player.sendMessage(disableMessage);
                        } else {
                            xrpgPlayer.setClassEnabled(true);
                            player.sendMessage(enableMessage);
                        }
                        return true;
                    default:
                        return false;
                }


            }

        }
        return false;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();
        if (strings.length == 1) {
            for (String tab : completions) {
                if (tab.toLowerCase().startsWith(strings[0].toLowerCase())) {
                    result.add(tab);
                }
            }
            return result;
        }
        return null;
    }
}
