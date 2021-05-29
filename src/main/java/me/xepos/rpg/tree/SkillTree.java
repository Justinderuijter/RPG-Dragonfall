package me.xepos.rpg.tree;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class SkillTree {

    private final String treeName;
    private final ItemStack icon;
    private final XRPG plugin;

    private final HashMap<String, SkillInfo> skills;
    private final ConfigurationSection interfaceSection;

    public SkillTree(String name, ConfigurationSection section, XRPG plugin){
        this.treeName = name;
        this.plugin = plugin;
        this.skills = new HashMap<>();

        String materialString = section.getString("icon", "BARRIER").toUpperCase();
        Material material = Material.getMaterial(materialString);
        if (material == null) material = Material.BARRIER;
        this.icon = new ItemStack(material);

        ItemMeta meta = this.icon.getItemMeta();
        meta.setDisplayName(treeName);
        //lore maybe

        this.interfaceSection = section.getConfigurationSection("interface");
        ConfigurationSection skillSection = section.getConfigurationSection("skills");

        if (skillSection != null){
            for (String skillId: skillSection.getKeys(false)) {
                SkillInfo info; // check incase this skill was already made by child skill
                if(skills.containsKey(skillId)) {
                    info = skills.get(skillId);
                } else {
                    info = new SkillInfo();
                    skills.put(skillId, info);
                }
                info.setRequired(skillSection.getStringList(skillId + ".requires"));
                info.setMaxLevel(skillSection.getInt(skillId + ".max-level", 1));

                for (String string:info.getRequired()) {
                    SkillInfo parent; // check if this skill has been made yet
                    if(skills.containsKey(string)) {
                        parent = skills.get(string);
                    } else {
                        parent = new SkillInfo();
                        skills.put(string, parent);
                    }

                    parent.getUnlocks().add(skillId); // add this skill into parent
                }
            }
        }
    }

    public List<String> getRequired(String skillId){
        List<String> req = skills.get(skillId).getRequired();
        if (req == null)
            req = new ArrayList<>();

        return req;
    }

    public List<String> getUnlocks(String skillId){
        List<String> ul = skills.get(skillId).getUnlocks();
        if (ul == null)
            ul = new ArrayList<>();

        return ul;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public @Nullable SkillInfo getSkillInfo(String skillId){
        return skills.get(skillId);
    }

    public Inventory getInventory(XRPGPlayer xrpgPlayer){
        Inventory inventory = Bukkit.createInventory(null, 54, "Skill Tree: " + treeName);

        buildTree(inventory, xrpgPlayer);

        return inventory;
    }

    private void buildTree(Inventory inventory, XRPGPlayer xrpgPlayer) {
        HashMap<String, XRPGSkill> playerSkills = xrpgPlayer.getAllLearnedSkills();

        List<String> layout = interfaceSection.getStringList("order");
        int rowNum = 0;
        for (String row : layout) {
            char[] chars = row.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == 'x') continue;

                String skillId = interfaceSection.getString("legend." + chars[i] + ".skill");
                final int maxLevel = skills.get(skillId).getMaxLevel();

                final XRPGSkill skill = playerSkills.get(skillId);
                int currentLevel = 0;
                if (skill != null){
                    currentLevel = skill.getSkillLevel();
                }

                ConfigurationSection skillData = plugin.getSkillData(skillId);

                Material material = Material.RED_WOOL;
                if (playerSkills.containsKey(skillId)) {
                    if (playerSkills.get(skillId).getSkillLevel() >= skills.get(skillId).getMaxLevel()){
                        material = Material.GREEN_WOOL;
                    }else { material = Material.ORANGE_WOOL; }
                }
                String nodeName = skillData.getString("name", "???") + " (" + currentLevel + "/" + maxLevel + ")";

                ItemStack item = Utils.buildItemStack(material, nodeName, skillData.getStringList("description"));

                setRequiredMeta(interfaceSection, item, skillId, currentLevel, maxLevel);

                inventory.setItem(i + (rowNum * 9), item);

            }
            rowNum++;
        }

        final List<String> description = new ArrayList<String>(){{
            add("Click to save your chances.");
            add("After saving you " + ChatColor.BOLD + "WILL NOT");
            add("be able to undo your changes.");
        }};

        final ItemStack saveBook = Utils.buildItemStack(Material.WRITABLE_BOOK, "Save skills", description);
        final ItemMeta meta = saveBook.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(plugin.getKey("separator"), PersistentDataType.BYTE, (byte) 1);
            saveBook.setItemMeta(meta);
        }

        inventory.setItem(inventory.getSize() - 1, saveBook);
    }

    private void setRequiredMeta(ConfigurationSection section, ItemStack item, String skillId, int level, int maxLevel) {
        ItemMeta meta = item.getItemMeta();


        meta.getPersistentDataContainer().set(plugin.getKey("skillId"), PersistentDataType.STRING, skillId);
        meta.getPersistentDataContainer().set(plugin.getKey("level"), PersistentDataType.INTEGER, level);
        meta.getPersistentDataContainer().set(plugin.getKey("maxLevel"), PersistentDataType.INTEGER, maxLevel);

        List<String> requiredSkills = section.getStringList("skills." + skillId + ".requires");
        if (requiredSkills.size() > 0) {

            StringBuilder requiredSkillsBuilder = new StringBuilder();
            for (String requiredSkill : requiredSkills) {
                requiredSkillsBuilder.append(requiredSkill).append(", ");
            }

            requiredSkillsBuilder.delete(requiredSkillsBuilder.length() - 1, requiredSkillsBuilder.length());

            meta.getPersistentDataContainer().set(plugin.getKey("requires"), PersistentDataType.STRING, requiredSkillsBuilder.toString());
        }

        item.setItemMeta(meta);
    }
}
