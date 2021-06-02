package me.xepos.rpg.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XRPGAdminCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("XRPGAdmin")){
            if (strings.length < 2) return false;

            switch(strings[1].toLowerCase()){
                case "add":
                    return true;
                case "remove":
                    return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

    private void subCommandAdd(String[] strings){
        switch (strings[2].toLowerCase()){
            case "level":
                return;
            case "experience":
                return;
            case "skill":
                return;
        }
    }
}
