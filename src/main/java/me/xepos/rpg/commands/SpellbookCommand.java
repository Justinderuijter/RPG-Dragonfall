package me.xepos.rpg.commands;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpellbookCommand implements TabExecutor {
    private final XRPG plugin;
    private final List<String> completions = new ArrayList<String>(){{
        add("open");
        add("get");
    }};

    public SpellbookCommand(XRPG plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("spellbook") ||command.getName().equalsIgnoreCase("sb")) {
            switch(strings.length){
                case 0:
                    return openSpellbook(commandSender);
                case 1:
                    if (strings[0].equalsIgnoreCase("open")){
                        return openSpellbook(commandSender);
                    }else if(strings[0].equalsIgnoreCase("get")){
                        if (commandSender instanceof Player){
                            ((Player)commandSender).getInventory().addItem(plugin.getSpellbookItem());
                            return true;
                        }
                    }
                    break;
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

    private boolean openSpellbook(CommandSender commandSender){
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be executed by players!");
            return true;
        }
        Player player = (Player) commandSender;
        XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer(player, true);

        if (xrpgPlayer == null) return true;

        Inventory inventory = Bukkit.createInventory(null, 27, "Spellbook");

        fillSeparator(inventory);

        fillKeybindSection(inventory, xrpgPlayer);

        fillAvailableSpells(inventory, xrpgPlayer);

        player.openInventory(inventory);
        return true;
    }

    private ItemStack createSpellBookItem(ConfigurationSection configurationSection){ ;
        final String iconName = configurationSection.getString("icon", "BARRIER").toUpperCase();
        final String skillName = configurationSection.getString("name", "Nameless Skill");
        return this.createSpellBookItem(Material.valueOf(iconName), skillName);
    }

    private ItemStack createSpellBookItem(Material material, String itemName){
        return Utils.buildItemStack(material, itemName, null);
    }

    private void fillSeparator(Inventory inventory){
        final ItemStack separator = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        final ItemStack saveIcon = new ItemStack(Material.WRITABLE_BOOK);
        final ItemMeta saveMeta = saveIcon.getItemMeta();
        final ItemMeta separatorMeta = separator.getItemMeta();

        saveMeta.setDisplayName("Save spellbook");
        saveMeta.getPersistentDataContainer().set(plugin.getKey("separator"), PersistentDataType.BYTE, (byte)1);
        saveIcon.setItemMeta(saveMeta);

        separatorMeta.getPersistentDataContainer().set(plugin.getKey("separator"), PersistentDataType.BYTE, (byte)1);
        separator.setItemMeta(separatorMeta);

        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, separator);
        }

        inventory.setItem(25, separator);
        inventory.setItem(26, saveIcon);
    }

    private void fillKeybindSection(Inventory inventory, XRPGPlayer xrpgPlayer){
        final int startIndex = inventory.getSize() - 9;
        for (int i = startIndex; i < xrpgPlayer.getSpellKeybinds().size() + startIndex; i++) {
            String keyBindSkillId = xrpgPlayer.getSpellKeybinds().get(i - startIndex);

            inventory.setItem(i, createSkillIcon(keyBindSkillId));
        }
    }

    private void fillAvailableSpells(Inventory inventory, XRPGPlayer xrpgPlayer){
        int counter = 0;
        for (String skillId:xrpgPlayer.getActiveHandler().getSkills().keySet()) {
            if (xrpgPlayer.getSpellKeybinds().contains(skillId)) continue;

            inventory.setItem(counter, createSkillIcon(skillId));

            counter++;

            if (counter >= 9) break;
        }
    }

    private ItemStack createSkillIcon(String skillId){
        final ItemStack itemStack = createSpellBookItem(plugin.getSkillData(skillId));
        final ItemMeta skillIconMeta = itemStack.getItemMeta();
        skillIconMeta.getPersistentDataContainer().set(plugin.getKey("skillId"), PersistentDataType.STRING, skillId);
        itemStack.setItemMeta(skillIconMeta);

        return itemStack;
    }
}
