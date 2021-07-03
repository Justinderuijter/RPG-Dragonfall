package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.DatabaseManager;
import me.xepos.rpg.datatypes.TreeData;
import me.xepos.rpg.tree.SkillTree;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeCommand implements TabExecutor {
    private final XRPG plugin;
    private final DatabaseManager databaseManager;
    private final List<String> completions = new ArrayList<String>(){{
        add("open");
        add("reset");
    }};

    public TreeCommand(XRPG plugin, DatabaseManager manager){
        this.plugin = plugin;
        this.databaseManager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equals("tree")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("This command can only be executed by players!");
                return true;
            }
            Player player = (Player) commandSender;
            XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player, true);
            if (xrpgPlayer == null) return false;

            if(strings.length == 0) {
                return openSkillTree(player, xrpgPlayer);
            }else if(strings.length == 1){
                if (strings[0].equalsIgnoreCase("reset")){
                    return xrpgPlayer.resetSkillTree();
                }else if(strings[0].equalsIgnoreCase("open")){
                    return openSkillTree(player, xrpgPlayer);
                }
            }
        }

        return false;
    }

    private boolean openSkillTree(Player player, XRPGPlayer xrpgPlayer){
        if (xrpgPlayer == null) {
            player.sendMessage(ChatColor.RED + "Cannot use this command!");
            return true;
        } else if (StringUtils.isEmpty(xrpgPlayer.getClassId())) {
            player.sendMessage("You do not currently have a class selected");
            return true;
        }

        String treeId = plugin.getClassInfo(xrpgPlayer.getClassId()).getSkillTreeId();

        SkillTree tree = plugin.getSkillTree(treeId);

        plugin.addTreeViewer(player.getUniqueId(), new TreeData(xrpgPlayer, treeId, tree, plugin));

        player.openInventory(tree.getInventory(xrpgPlayer));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();
        if (strings.length == 1){
            for (String tab:completions) {
                if (tab.toLowerCase().startsWith(strings[0].toLowerCase())){
                    result.add(tab);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
}
