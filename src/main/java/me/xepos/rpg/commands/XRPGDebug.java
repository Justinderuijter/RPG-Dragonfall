package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XRPGDebug extends BaseCommand {
    private final ChatColor primaryColor = ChatColor.YELLOW;
    private final ChatColor secondaryColor = ChatColor.GREEN;
    private final XRPG plugin;

    private final List<String> modifiers = new ArrayList<String>() {{
        add("projectiles");
        add("players");
        add("skills");
        add("modifiers");
        add("dt");
        add("logintasks");
    }};

    public XRPGDebug(XRPG plugin) {
        super("debug");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (command.getName().equals("xrpgdebug")) {
            switch(strings.length){
                case 1:
                    if(strings[0].equalsIgnoreCase("projectiles")){
                        commandSender.sendMessage(primaryColor + "Projectiles tracked: " + secondaryColor + plugin.projectiles.size());
                        return true;
                    }else if (strings[0].equalsIgnoreCase("players")){
                        StringBuilder playerList = new StringBuilder();
                        for (XRPGPlayer xrpgPlayer:plugin.getPlayerManager().getXRPGPlayers().values()) {
                            playerList.append(primaryColor).append(xrpgPlayer.getPlayer().getName()).append(ChatColor.WHITE).append(" (").append(secondaryColor).append(xrpgPlayer.getClassDisplayName()).append(ChatColor.WHITE).append(")").append(", ");
                        }

                        int length = playerList.length();
                        if (length < 1){
                            playerList.delete(length - 2, length);
                        }

                        commandSender.sendMessage(primaryColor + "Players tracked" + ChatColor.WHITE + "(" + secondaryColor + plugin.getPlayerManager().getXRPGPlayers().size() + ChatColor.WHITE + ")" + primaryColor + ":");
                        commandSender.sendMessage(playerList.toString());
                        return true;
                    }else if (strings[0].equalsIgnoreCase("skills")){
                        StringBuilder skillList = new StringBuilder();
                        for (String skillId:plugin.getAllLoadedSkillIds()) {
                            skillList.append(skillId).append(", ");
                        }

                        int length = skillList.length();
                        if (length < 1){
                            skillList.delete(length - 2, length);
                        }

                        commandSender.sendMessage(primaryColor + "Loaded skills " + ChatColor.WHITE + "(" + secondaryColor + plugin.getSkillData().size() + ChatColor.WHITE + ")" + primaryColor + ":");
                        commandSender.sendMessage(skillList.toString());
                        return true;
                    }else if (strings[0].equalsIgnoreCase("logintasks")){
                        commandSender.sendMessage(primaryColor + "Currently " + ChatColor.RED + plugin.getPlayerManager().getLoginTaskCount() + primaryColor + " login tasks in memory");
                        return true;
                    }
                case 2:
                    Player player = Bukkit.getPlayer(strings[1]);
                    if (player == null){
                        commandSender.sendMessage(ChatColor.RED + "Could not find player: " + strings[1]);
                        return true;
                    }

                    if (strings[0].equalsIgnoreCase("modifiers")){
                        for (Attribute attribute:Attribute.values()) {
                            AttributeInstance attributeInstance = player.getAttribute(attribute);
                            if (attributeInstance != null){
                                for (AttributeModifier modifier:attributeInstance.getModifiers()) {
                                    commandSender.sendMessage(primaryColor + "Name: " + secondaryColor + modifier.getName() + ChatColor.WHITE + " | " + primaryColor + "Attribute: " + secondaryColor + attribute.name());
                                    commandSender.sendMessage(primaryColor + "Operation: " + secondaryColor + modifier.getOperation().name() + ChatColor.WHITE + " | " + primaryColor + "Amount: " + secondaryColor + modifier.getAmount());
                                    commandSender.sendMessage(primaryColor + "UUID: " + secondaryColor + modifier.getUniqueId().toString());

                                }
                            }
                        }
                        return true;
                    }else if(strings[0].equalsIgnoreCase("dt")){
                        XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(player, true);
                        if (xrpgPlayer == null){
                            commandSender.sendMessage(ChatColor.RED + StringUtils.capitalise(player.getName()) + ChatColor.RED + " is not a valid XRPG player!");
                            return true;
                        }

                        commandSender.sendMessage(ChatColor.RED + "Damage Taken multiplier for " + player.getPlayer() + ChatColor.RED + ": " + String.format("%.2f", xrpgPlayer.getDamageTakenMultiplier()));
                        return true;
                    }
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();

        if (strings.length == 1){
            for (String tab : modifiers) {
                if (tab.toLowerCase().startsWith(strings[0].toLowerCase())) {
                    result.add(tab);
                }
            }
            return result;
        }else if(strings.length == 2 && strings[0].equalsIgnoreCase("modifiers")){
            return null;
        }else {
            return Collections.emptyList();
        }
    }
}
