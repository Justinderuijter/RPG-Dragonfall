package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XRPGAdminCommand extends BaseCommand {
    private final XRPG plugin;
    private final SkillLoader skillLoader;

    private final List<String> modifiers = new ArrayList<String>() {{
        add("add");
        //add("remove");
        add("set");
        add("create");
        add("reset");
    }};

    private final List<String> options = new ArrayList<String>() {{
        add("level");
        add("experience");
        add("skill");
    }};

    public XRPGAdminCommand(XRPG plugin, SkillLoader skillLoader) {
        super("admin");
        this.plugin = plugin;
        this.skillLoader = skillLoader;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("XRPGAdmin")) {
            if (strings.length < 4) return false;
            final Player target = Bukkit.getPlayer(strings[0]);

            if (target == null) {
                commandSender.sendMessage(ChatColor.RED + strings[0] + " was not found! Are they online?");
                return true;
            }

            final XRPGPlayer xrpgTarget = plugin.getXRPGPlayer(target, true);
            if (xrpgTarget == null) {
                return true;
            }

            switch (strings[1].toLowerCase()) {
                case "add":
                    return subCommandAdd(commandSender, xrpgTarget, strings);
                case "remove":
                    return true;
                case "set":
                    return subCommandSet(commandSender, xrpgTarget, strings);
                case "create":
                    return subCommandCreate(commandSender, xrpgTarget, strings);
                case "reset":
                    return subCommandReset(commandSender, xrpgTarget, strings);
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> result = new ArrayList<>();

        switch (strings.length) {
            case 1:
                return null;
            case 2:
                for (String tab : modifiers) {
                    if (tab.toLowerCase().startsWith(strings[1].toLowerCase())) {
                        result.add(tab);
                    }
                }
                return result;
            case 3:

                switch (strings[1].toLowerCase()){
                    case "set":
                        return new ArrayList<String>() {{
                            add("class");
                        }};
                    case "create":
                        return new ArrayList<String>() {{
                            add("eventspell");
                        }};
                    case "reset":
                        return new ArrayList<String>() {{
                            add("skilltree");
                        }};
                    default:
                        for (String tab : options) {
                            if (tab.toLowerCase().startsWith(strings[2].toLowerCase())) {
                                result.add(tab);
                            }
                        }
                        return result;
                }
            case 4:
                if (strings[1].equalsIgnoreCase("set") && strings[2].equalsIgnoreCase("class")){
                    for (String tab:plugin.getClassInfo().keySet()) {
                        if (tab.toLowerCase().startsWith(strings[3].toLowerCase())){
                            result.add(tab);
                        }
                    }
                }
                else if (strings[2].equalsIgnoreCase("skill") || strings[2].equalsIgnoreCase("eventspell")){
                    for (String tab : plugin.getAllLoadedSkillIds()) {
                        if (tab.toLowerCase().startsWith(strings[3].toLowerCase())) {
                            result.add(tab);
                        }
                    }
                }
                return result;
        }
        return Collections.emptyList();

    }

    private boolean subCommandAdd(CommandSender sender, XRPGPlayer xrpgTarget, String[] strings) {

        switch (strings[2].toLowerCase()) {
            case "level":
                if (!checkPermissions(sender, "add.level")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }
                try {
                    final int level = Integer.parseInt(strings[3]);
                    xrpgTarget.addLevels(level);
                    xrpgTarget.getPlayer().sendMessage("You gained " + level + " experience for " + xrpgTarget.getClassDisplayName() + "!");
                    return true;
                } catch (NumberFormatException exception) {
                    sender.sendMessage(strings[3] + " is not a valid number!");
                }
                return false;
            case "experience":
                if (!checkPermissions(sender, "add.level")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }
                try {
                    final double experience = Double.parseDouble(strings[3]);
                    xrpgTarget.addExp(experience);
                    xrpgTarget.getPlayer().sendMessage("You gained " + experience + " experience for " + xrpgTarget.getClassDisplayName() + "!");
                    return true;
                } catch (NumberFormatException exception) {
                    sender.sendMessage(strings[3] + " is not a valid number!");
                }
                return false;
            case "skill":
                if (!checkPermissions(sender, "add.skill")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }
                int skillLevel = 1;
                if (strings.length == 5) {
                    skillLevel = Integer.parseInt(strings[4]);
                }
                if (plugin.hasSkillData(strings[3])) {
                    skillLoader.addSkillToPlayer(strings[3], xrpgTarget, skillLevel, false);
                    xrpgTarget.getPlayer().sendMessage(ChatColor.GREEN + "You received the skill " + plugin.getSkillData(strings[3]).getName() + "! It is now level " + skillLevel + "!");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Couldn't find skill: " + strings[3] + "!");
                return true;
        }
        return false;
    }

    private boolean subCommandSet(CommandSender sender, XRPGPlayer xrpgTarget, String[] strings) {
        if (strings[2].equalsIgnoreCase("class")) {
            if (strings.length < 5) {
                plugin.getClassChangeManager().changeClass(xrpgTarget, strings[3], true);
                return true;
            }
            plugin.getClassChangeManager().changeClass(xrpgTarget, strings[3], Boolean.parseBoolean(strings[4]));
            return true;
        }
        return false;
    }

    public boolean subCommandCreate(CommandSender sender, XRPGPlayer xrpgTarget, String[] strings){
        if (strings.length == 4){
            if (strings[2].equalsIgnoreCase("eventspell")){
                if(!checkPermissions(sender, "create.eventspell")){
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return true;
                }

                String skillId = strings[3];
                SkillData skillData = plugin.getSkillData(skillId);
                if (skillData != null){
                    List<String> lore = new ArrayList<String>(){{
                        add("Hold and right click this item");
                        add("To learn this skill or level it up");
                        add("Can only be learned by the following classes:");
                        add("- ALL");
                    }};
                    ItemStack spellItem = Utils.buildItemStack(Material.ENCHANTED_BOOK, "Event Spell: " + skillData.getName(), lore);
                    ItemMeta spellItemMeta = spellItem.getItemMeta();
                    spellItemMeta.getPersistentDataContainer().set(plugin.getKey("skillId"), PersistentDataType.STRING, skillId);
                    spellItem.setItemMeta(spellItemMeta);

                    Inventory targetInventory = xrpgTarget.getPlayer().getInventory();
                    if (targetInventory.firstEmpty() != -1){
                        targetInventory.addItem(spellItem);
                        xrpgTarget.getPlayer().sendMessage(ChatColor.GREEN + "You received " + ChatColor.BOLD + "Event Spell: " + skillData.getName() + "!");
                    }else{
                        sender.sendMessage(ChatColor.RED + "Target's inventory is full!");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean subCommandReset(CommandSender sender, XRPGPlayer xrpgTarget, String[] strings){
        if (strings.length == 3){
            if (!checkPermissions(sender, "reset.skilltree")){
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }
            if (plugin.isTreeViewer(xrpgTarget.getPlayer())){
                sender.sendMessage(ChatColor.RED + "This player is currently editing their skill tree!");
                return true;
            }
            xrpgTarget.resetSkillTree();
            return true;
        }
        return false;
    }
}
