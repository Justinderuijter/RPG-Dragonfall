package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.SkillLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XRPGReload extends BaseCommand {
    private final XRPG plugin;
    private final SkillLoader skillLoader;

    public XRPGReload(XRPG plugin, SkillLoader skillLoader){
        super("admin");
        this.plugin = plugin;
        this.skillLoader = skillLoader;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if (command.getName().equals("xrpgreload") && checkPermissions(commandSender, "reload"))
        {
            if (strings.length == 1){
                switch(strings[0].toLowerCase()){
                    case "config":
                        plugin.reloadConfig();
                        commandSender.sendMessage(ChatColor.GREEN + "Config.yml reloaded!");
                        commandSender.sendMessage(ChatColor.GREEN + "Keep in mind that some things can only be reloaded by restarting the server!");
                        commandSender.sendMessage(ChatColor.RED + "This command is untested and may cause issues!");
                        return true;
                    case "skills":
                        plugin.getSkillData().putAll(skillLoader.initializeSkills());
                        commandSender.sendMessage(ChatColor.GREEN + "Skills reloaded!");
                        commandSender.sendMessage(ChatColor.GREEN + "Keep in mind that some things can only be reloaded by restarting the server!");
                        commandSender.sendMessage(ChatColor.RED + "This command is untested and may cause issues!");
                        return true;
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {
        return null;
    }
}
