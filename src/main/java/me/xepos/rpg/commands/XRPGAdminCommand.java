package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class XRPGAdminCommand implements TabExecutor {
    private final XRPG plugin;
    private final SkillLoader skillLoader;

    private final List<String> modifiers = new ArrayList<String>(){{
        add("add");
        //add("remove");
    }};

    private final List<String> options = new ArrayList<String>(){{
        add("level");
        add("experience");
        add("skill");
    }};

    public XRPGAdminCommand(XRPG plugin, SkillLoader skillLoader){
        this.plugin = plugin;
        this.skillLoader = skillLoader;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("XRPGAdmin")){
            if (strings.length < 4) return false;
            final Player target = Bukkit.getPlayer(strings[0]);

            if (target == null){
                commandSender.sendMessage(ChatColor.RED + strings[0] + " was not found! Are they online?");
                return true;
            }

            final XRPGPlayer xrpgTarget = plugin.getXRPGPlayer(target, true);
            if (xrpgTarget == null){
                return true;
            }

            switch(strings[1].toLowerCase()){
                case "add":
                    return subCommandAdd(commandSender, xrpgTarget, strings);
                case "remove":
                    return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();

        switch(strings.length){
            case 1:
                return null;
            case 2:
                for (String tab:modifiers) {
                    if (tab.toLowerCase().startsWith(strings[1].toLowerCase())){
                        result.add(tab);
                    }
                }
                return result;
            case 3:
                for (String tab:options) {
                    if (tab.toLowerCase().startsWith(strings[2].toLowerCase())){
                        result.add(tab);
                    }
                }
                return result;
            case 4:
                if (strings[2].equalsIgnoreCase("skill")){
                    for (String tab:plugin.getAllLoadedSkillIds()) {
                        if (tab.toLowerCase().startsWith(strings[3].toLowerCase())){
                            result.add(tab);
                        }
                    }
                    return result;
                }
        }
        return null;
    }

    private boolean subCommandAdd(CommandSender sender, XRPGPlayer xrpgTarget, String[] strings){

        switch (strings[2].toLowerCase()){
            case "level":
                if (!checkPermissions(sender, "add.level")){
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }
                try{
                    final int level = Integer.parseInt(strings[3]);
                    xrpgTarget.addLevels(level);
                    xrpgTarget.getPlayer().sendMessage("You gained " + level + " experience for " + xrpgTarget.getClassDisplayName() + "!");
                    return true;
                }catch(NumberFormatException exception){
                    sender.sendMessage(strings[3] + " is not a valid number!");
                }
                return false;
            case "experience":
                if (!checkPermissions(sender, "add.exp")){
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }
                try{
                    final double experience = Double.parseDouble(strings[3]);
                    xrpgTarget.addExp(experience);
                    xrpgTarget.getPlayer().sendMessage("You gained " + experience + " experience for " + xrpgTarget.getClassDisplayName() + "!");
                    return true;
                }catch(NumberFormatException exception){
                    sender.sendMessage(strings[3] + " is not a valid number!");
                }
                return false;
            case "skill":
                if (!checkPermissions(sender, "add.level")){
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }
                int skillLevel = 1;
                if (strings.length == 5){
                    skillLevel = Integer.parseInt(strings[4]);
                }
                if (plugin.hasSkillData(strings[3])){
                    skillLoader.addSkillToPlayer(strings[3], xrpgTarget, skillLevel);
                    xrpgTarget.getPlayer().sendMessage(ChatColor.GREEN + "You received the skill " + plugin.getSkillData(strings[3]).getString("name", "???") + "! It is now level " + skillLevel + "!");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Couldn't find skill: " + strings[3] + "!");
                return true;
        }
        return false;
    }

    private boolean checkPermissions(CommandSender sender, String childPermission){
        final String wildcard = "xrpg.*";
        final String base = "xrpg.admin";
        if (StringUtils.isBlank(childPermission))
            return sender.hasPermission(wildcard) || sender.hasPermission(base);
        else
            return sender.isOp() || sender.hasPermission(wildcard) || sender.hasPermission(base) || sender.hasPermission(base + "." + childPermission);
    }
}
