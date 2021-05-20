package me.xepos.rpg.commands;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class XRPGDebug implements CommandExecutor {

    private final XRPG plugin;
    private final HashMap<String, FileConfiguration> classData;

    public XRPGDebug(XRPG plugin, HashMap<String, FileConfiguration> classData) {
        this.plugin = plugin;
        this.classData = classData;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (command.getName().equals("xrpgdebug")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if(strings.length == 1)
                {
                    switch (strings[0])
                    {
                        case "fireballs":
                            player.sendMessage("Fireballs: " + plugin.projectiles.size());
                            return true;
                        case "damagetaken":
                            for (String d : plugin.getXRPGPlayer(player).dmgTakenMultipliers.keySet()) {
                                player.sendMessage(plugin.getXRPGPlayer(player).dmgTakenMultipliers.get(d).toString());
                            }
                            player.sendMessage("dmgTakenMP" + plugin.getXRPGPlayer(player).dmgTakenMultipliers.size());
                            return true;
                        case "modifiers":
                            for (String identifier : AttributeModifierManager.getInstance().getModifiers(ModifierType.POSITIVE).keySet()) {
                                player.sendMessage(AttributeModifierManager.getInstance().get(ModifierType.POSITIVE, identifier).getAttributeModifier().toString());
                            }

                            for (String identifier : AttributeModifierManager.getInstance().getModifiers(ModifierType.NEGATIVE).keySet()) {
                                player.sendMessage(AttributeModifierManager.getInstance().get(ModifierType.NEGATIVE, identifier).getAttributeModifier().toString());
                            }
                            return true;
                        case "players":
                            for (UUID id : plugin.getRPGPlayers().keySet()) {
                                XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(id);
                                player.sendMessage(xrpgPlayer.getPlayer().getName() + ": " + xrpgPlayer.getClassId());
                            }
                            return true;
                        case "skilldata":
                            HashMap<String, PassiveEventHandler> handlers = plugin.getXRPGPlayer(player).getPassiveHandlerList();
                            for (XRPGSkill skill:plugin.getXRPGPlayer(player).getActiveHandler().getSkills().values()) {
                                player.sendMessage(skill.getName());
                            }
                            for (String handlerName : handlers.keySet()) {
                                for (XRPGSkill skill : handlers.get(handlerName).getSkills().values()) {
                                    player.sendMessage(skill.getName());
                                }
                            }
                            return true;
                        case "keybinds":
                            int counter = 0;
                            for (String name:plugin.getXRPGPlayer(player).getSpellKeybinds()) {
                                player.sendMessage(counter + ": " + name);
                                counter++;
                            }
                            return true;
                        case "classes":
                            for (String classId : classData.keySet()) {
                                player.sendMessage(classId);
                            }

                            return true;

                        case "clear":
                            plugin.getXRPGPlayer(player).getSpellKeybinds().clear();
                            return true;
                        case "skill":
                            for (String skillId:plugin.getAllSkills()) {
                                player.sendMessage(skillId);
                            }
                        default:
                            return false;
                    }
                }
            }

        }
        return false;
    }
}
