package me.xepos.rpg.commands;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.Nimble;
import me.xepos.rpg.skills.base.XRPGSkill;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class XRPGDebug implements CommandExecutor {

    private final XRPG plugin;

    public XRPGDebug(XRPG plugin) {
        this.plugin = plugin;
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
                            for (String d : plugin.getXRPGPlayer(player, true).dmgTakenMultipliers.keySet()) {
                                player.sendMessage(plugin.getXRPGPlayer(player, true).dmgTakenMultipliers.get(d).toString());
                            }
                            player.sendMessage("dmgTakenMP" + plugin.getXRPGPlayer(player, true).dmgTakenMultipliers.size());
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
                                XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(id, true);
                                player.sendMessage(xrpgPlayer.getPlayer().getName() + ": " + xrpgPlayer.getClassId());
                            }
                            return true;
                        case "skilldata":
                            HashMap<String, PassiveEventHandler> handlers = plugin.getXRPGPlayer(player, true).getPassiveHandlerList();
                            for (XRPGSkill skill:plugin.getXRPGPlayer(player, true).getActiveHandler().getSkills().values()) {
                                player.sendMessage(skill.getSkillName() + ": " + skill.getSkillLevel());
                            }
                            for (String handlerName : handlers.keySet()) {
                                for (XRPGSkill skill : handlers.get(handlerName).getSkills().values()) {
                                    player.sendMessage(skill.getSkillName() + ": " + skill.getSkillLevel());
                                }
                            }
                            return true;
                        case "keybinds":
                            int counter = 0;
                            for (String name:plugin.getXRPGPlayer(player, true).getSpellKeybinds()) {
                                player.sendMessage(counter + ": " + name);
                                counter++;
                            }
                            return true;
                        case "clear":
                            plugin.getXRPGPlayer(player, true).getSpellKeybinds().clear();
                            return true;
                        case "skill":
                            for (String skillId:plugin.getAllLoadedSkillIds()) {
                                player.sendMessage(skillId);
                            }
                        default:
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(strings[0]));
                            return false;
                    }
                }else if(strings.length == 2){
                    XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player, true);


                    new Nimble(xrpgPlayer, plugin.getSkillData("Nimble"), plugin, 1);
                }
            }

        }
        return false;
    }
}
