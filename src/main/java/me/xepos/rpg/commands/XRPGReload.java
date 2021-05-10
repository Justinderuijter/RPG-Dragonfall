package me.xepos.rpg.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class XRPGReload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if (command.getName().equals("xrpgreload"))
        {
            commandSender.sendMessage("XRPG has been reloaded!");
            return true;
        }
        return false;
    }
}
