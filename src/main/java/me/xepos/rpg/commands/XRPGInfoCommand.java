package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class XRPGInfoCommand implements TabExecutor {
    private final XRPG plugin;
    private final List<String> modifiers = new ArrayList<String>() {{
        add("player");
        //add("skill");
    }};

    public XRPGInfoCommand(XRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("XRPGInfo")) {
            switch (strings.length) {
                case 0:
                    if (commandSender instanceof Player) {
                        displayPlayerStats(commandSender, (((Player) commandSender).getPlayer()));
                        return true;
                    }
                    commandSender.sendMessage("Please specify a player or skill");
                    return false;


                case 2:
                    if (strings[0].equalsIgnoreCase("player")) {
                        Player target = Bukkit.getPlayer(strings[1]);
                        if (target == null) {
                            commandSender.sendMessage("Target not found!");
                            return true;
                        }

                        return displayPlayerStats(commandSender, target);

                    } else if (strings[0].equalsIgnoreCase("skill")) {

                    }
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();

        switch (strings.length){
            case 1:
                for (String tab : modifiers) {
                    if (tab.toLowerCase().startsWith(strings[0].toLowerCase())) {
                        result.add(tab);
                    }
                }
                return result;
            case 2:
                return null;
            default:
                return Collections.emptyList();
        }
    }

    private boolean displayPlayerStats(CommandSender commandSender, Player target) {
        final ChatColor primaryColor = ChatColor.YELLOW;

        XRPGPlayer xrpgTarget = plugin.getPlayerManager().getXRPGPlayer(target, true);
        boolean querySelf = false;
        if ((commandSender instanceof Player && commandSender.equals(target)) || commandSender.isOp()) {
            querySelf = true;
        }

        if (xrpgTarget == null) {
            commandSender.sendMessage("Target player is not an XRPG player!");
            return true;
        }

        commandSender.sendMessage(primaryColor + "----------[" + ChatColor.RED + target.getDisplayName() + primaryColor + "]----------");
        if (StringUtils.isNotBlank(xrpgTarget.getClassId())){
            commandSender.sendMessage(primaryColor + "Class: " + ChatColor.GREEN + xrpgTarget.getClassDisplayName());
            commandSender.sendMessage(primaryColor + "Level: " + ChatColor.GREEN + xrpgTarget.getLevel());
            commandSender.sendMessage(primaryColor + "Experience: " + ChatColor.GREEN +  String.format("%.1f", xrpgTarget.getCurrentExp()) + ChatColor.WHITE + "/" + ChatColor.GREEN + xrpgTarget.getRequiredExpToLevel(xrpgTarget.getLevel()));
            //commandSender.sendMessage(primaryColor + "PvE Multiplier: " + ChatColor.GREEN + String.format("%.2f", 1 + xrpgTarget.getLevel() * plugin.getDamageMultiplier()));
            commandSender.sendMessage(primaryColor + "PvE Multiplier (spells): " + ChatColor.GREEN + String.format("%.2f", 1 + xrpgTarget.getLevel() * plugin.getSpellDamageMultiplier()));
            commandSender.sendMessage(primaryColor + "Health Level: " + ChatColor.GREEN + xrpgTarget.getHealthLevel() + ChatColor.WHITE + " | " + primaryColor + "Mana Level: " + ChatColor.GREEN + xrpgTarget.getManaLevel());
        }else{
            commandSender.sendMessage(primaryColor + "Class: " + ChatColor.GREEN + "None");
        }

        commandSender.sendMessage(primaryColor + "Mana: " + ChatColor.BLUE + xrpgTarget.getCurrentMana() + ChatColor.WHITE + "/" + ChatColor.BLUE + xrpgTarget.getMaximumMana());

        if (querySelf) {
            commandSender.sendMessage(primaryColor + "Unlock Points: " + colorIntStringViaValue(xrpgTarget.getSkillUnlockPoints()));
            commandSender.sendMessage(primaryColor + "Upgrade Points: " + colorIntStringViaValue(xrpgTarget.getSkillUpgradePoints()));
        }

        StringBuilder skillString = new StringBuilder();
        HashMap<String, XRPGSkill> skillHashMap = xrpgTarget.getAllLearnedSkills();
        for (String skillId:skillHashMap.keySet()) {
            XRPGSkill skill = skillHashMap.get(skillId);
            skillString.append(ChatColor.GREEN).append(skill.getSkillName()).append(ChatColor.WHITE);
            if (skill.isEventSkill()){
                skillString.append("*");
            }
            skillString.append(" (").append(ChatColor.GREEN).append(skill.getSkillLevel()).append(ChatColor.WHITE).append("), ");
        }
        if (skillString.length() > 2) {
            skillString = new StringBuilder(skillString.substring(0, skillString.lastIndexOf(", ")));
        }
        commandSender.sendMessage(primaryColor + "Skills: " + skillString);


        return true;
    }

    private String colorIntStringViaValue(int value) {
        if (value <= 0) {
            return ChatColor.RED + String.valueOf(value);
        }
        return ChatColor.GREEN + String.valueOf(value);
    }
}
