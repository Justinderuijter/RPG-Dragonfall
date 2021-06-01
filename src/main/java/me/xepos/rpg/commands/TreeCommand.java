package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.TreeData;
import me.xepos.rpg.tree.SkillTree;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TreeCommand implements CommandExecutor {
    private final XRPG plugin;

    public TreeCommand(XRPG plugin){
        this.plugin = plugin;
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
            if (xrpgPlayer == null){
                player.sendMessage(ChatColor.RED + "Cannot use this command!");
                return true;
            }else if (StringUtils.isEmpty(xrpgPlayer.getClassId())){
                player.sendMessage("You do not currently have a class selected");
                return true;
            }

            String treeId = plugin.getClassInfo(xrpgPlayer.getClassId()).getSkillTreeId();

            SkillTree tree = plugin.getSkillTree(treeId);

            plugin.addTreeViewer(player.getUniqueId(), new TreeData(xrpgPlayer, treeId, tree, plugin));

            player.openInventory(tree.getInventory(xrpgPlayer));
            return true;
        }

        return false;
    }
}
