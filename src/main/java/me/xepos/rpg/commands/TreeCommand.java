package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.database.tasks.SavePlayerDataTask;
import me.xepos.rpg.datatypes.TreeData;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.tree.SkillTree;
import me.xepos.rpg.utils.Utils;
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
    private final IDatabaseManager databaseManager;
    private final List<String> completions = new ArrayList<String>(){{
        add("open");
        add("reset");
    }};

    public TreeCommand(XRPG plugin, IDatabaseManager manager){
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
                    return resetPlayerSkills(player, xrpgPlayer);
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

    private boolean resetPlayerSkills(Player player, XRPGPlayer xrpgPlayer){
        byte upgradePointsToRefund = 0;
        byte unlockPointsToRefund = 0;

        for (String skillId :xrpgPlayer.getActiveHandler().getSkills().keySet()) {
            final int skillLevel = xrpgPlayer.getActiveHandler().getSkills().get(skillId).getSkillLevel();

            if (skillLevel > 1){
                upgradePointsToRefund += skillLevel -1;
                unlockPointsToRefund++;
            }else if (skillLevel == 1){
                unlockPointsToRefund++;
            }
        }
        xrpgPlayer.getActiveHandler().clear();

        for (String handlerName :xrpgPlayer.getPassiveHandlerList().keySet()) {
            PassiveEventHandler handler = xrpgPlayer.getPassiveEventHandler(handlerName);
            for (String skillId:handler.getSkills().keySet()) {
                final int skillLevel = handler.getSkills().get(skillId).getSkillLevel();

                if (skillLevel > 1){
                    upgradePointsToRefund += skillLevel -1;
                    unlockPointsToRefund++;
                }else if (skillLevel == 1){
                    unlockPointsToRefund++;
                }

            }
            handler.clear();
        }

        upgradePointsToRefund += xrpgPlayer.getHealthLevel();
        upgradePointsToRefund += xrpgPlayer.getManaLevel();

        xrpgPlayer.setHealthLevel(0);
        xrpgPlayer.setManaLevel(0);

        xrpgPlayer.addSkillUpgradePoints(upgradePointsToRefund);
        xrpgPlayer.addSkillUnlockPoints(unlockPointsToRefund);

        Utils.removeAllModifiers(player);

        new SavePlayerDataTask(databaseManager, xrpgPlayer).runTaskAsynchronously(plugin);

        player.sendMessage(ChatColor.GREEN + "You successfully reset your skill points!");
        player.sendMessage(ChatColor.GREEN + "You have been refunded " + upgradePointsToRefund + " upgrade points and " + unlockPointsToRefund + " unlock points!");

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
