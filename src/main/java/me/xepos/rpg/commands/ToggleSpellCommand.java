package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.utils.SpellmodeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToggleSpellCommand implements TabExecutor {
    private final XRPG plugin;
    private final List<String> completions = new ArrayList<String>(){{
        add("on");
        add("off");
        add("enable");
        add("disable");
        add("toggle");
        add("status");
    }};

    public ToggleSpellCommand(XRPG plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("spellmode") || command.getName().equalsIgnoreCase("sm")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("This command can only be executed by players!");
                return true;
            }
            if (strings.length != 1) return false;

            Player player = (Player) commandSender;
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player, true);
            if (xrpgPlayer == null) {
                return true;
            }

            switch (strings[0].toLowerCase()) {
                case "on":
                case "enable":
                    SpellmodeUtils.enterSpellmode(xrpgPlayer);
                    return true;
                case "off":
                case "disable":
                    SpellmodeUtils.disableSpellmode(xrpgPlayer);
                    return true;
                case "toggle":
                    if (xrpgPlayer.isSpellCastModeEnabled()){
                        SpellmodeUtils.disableSpellmode(xrpgPlayer);
                        return true;
                    }
                    SpellmodeUtils.enterSpellmode(xrpgPlayer);
                    return true;
                default:
                    commandSender.sendMessage("Spellcast mode is " + (xrpgPlayer.isSpellCastModeEnabled() ? "enabled." : "disabled."));
                    return true;
            }
        }
        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();
        if (strings.length == 1){
            for (String tab:completions) {
                if (tab.toLowerCase().startsWith(strings[0].toLowerCase())){
                    result.add(tab);
                }
            }
            return result;
        }
        return null;
    }

}
