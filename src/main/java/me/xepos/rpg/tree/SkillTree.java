package me.xepos.rpg.tree;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.TreeCache;
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

    private TreeCache treeCache;
    private final HashMap<String, SkillInfo> skills;
    private final ConfigurationSection interfaceSection;

    public SkillTree(String name, ConfigurationSection section, XRPG plugin){
        this.treeName = name;
        this.plugin = plugin;
        this.skills = new HashMap<>();

        String materialString = section.getString("icon", "BARRIER").toUpperCase();
        Material material = Material.getMaterial(materialString);
        if (material == null) material = Material.BARRIER;
        this.icon = Utils.buildItemStack(material, treeName, null);

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
        if (this.treeCache == null){
            this.treeCache = new TreeCache();
        }

        HashMap<String, XRPGSkill> playerSkills = xrpgPlayer.getAllLearnedSkills();
        List<String> layout = interfaceSection.getStringList("order");

        String lastPathItemName = "";
        ItemStack cachedItem = null;

        int rowNum = 0;
        for (String row : layout) {
            char[] chars = row.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == 'x' || chars[i] == '-') continue;
                final int itemIndex = i + (rowNum * 9);

                String skillId = interfaceSection.getString("legend." + chars[i] + ".skill");
                if (skillId == null){
                    //check if it's a path indicator
                    String currentPathItemName = interfaceSection.getString("legend." + chars[i] + ".path-item");
                    if (lastPathItemName.equalsIgnoreCase(currentPathItemName)){
                        inventory.setItem(itemIndex, cachedItem);
                    }else{
                        Material material;
                        try{
                            material = Material.valueOf(currentPathItemName);
                        }catch(IllegalArgumentException ex){
                            material = Material.CYAN_STAINED_GLASS_PANE;
                        }
                        ItemStack item = Utils.buildItemStack(material, "", null);

                        //Caching
                        lastPathItemName = currentPathItemName;
                        cachedItem = item;

                        inventory.setItem(itemIndex, item);
                    }


                }else{
                    if (skills.get(skillId) == null){
                        Bukkit.getLogger().info(skillId + " is null!");
                        continue;
                    }

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
                    if (skillData == null){
                        Bukkit.getLogger().info("Skilldata is null for " + skillId);
                    }
                    String nodeName = skillData.getString("name", "???") + " (" + currentLevel + "/" + maxLevel + ")";

                    ItemStack item = Utils.buildItemStack(material, nodeName, skillData.getStringList("description"));

                    setRequiredMeta(interfaceSection, item, skillId, currentLevel, maxLevel);


                    inventory.setItem(itemIndex, item);

                    if (!this.treeCache.contains(skillId)){
                        this.treeCache.addToCache(skillId, itemIndex);
                    }
                }

            }
            rowNum++;
        }

        final List<String> description = new ArrayList<String>(){{
            add("Click to save your changes.");
            add("After saving you " + ChatColor.BOLD + "WILL NOT");
            add("be able to undo your changes.");
        }};

        final ItemStack saveBook = Utils.buildItemStack(Material.WRITABLE_BOOK, "Save skills", description);
        final ItemMeta meta = saveBook.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(plugin.getKey("separator"), PersistentDataType.BYTE, (byte) 1);
            saveBook.setItemMeta(meta);
        }

        final ItemStack health = Utils.buildItemStack(Material.RED_WOOL, "Increase Health", null);
        final ItemMeta healthMeta = health.getItemMeta();
        if (healthMeta != null) {
            healthMeta.getPersistentDataContainer().set(plugin.getKey("attribute"), PersistentDataType.STRING, "health");
            health.setItemMeta(healthMeta);
        }

        final ItemStack mana = Utils.buildItemStack(Material.BLUE_WOOL, "Increase Mana", null);
        final ItemMeta manaMeta = mana.getItemMeta();
        if (manaMeta != null) {
            manaMeta.getPersistentDataContainer().set(plugin.getKey("attribute"), PersistentDataType.STRING, "mana");
            mana.setItemMeta(manaMeta);
        }

        final int size = inventory.getSize();
        inventory.setItem(8, saveBook);
        inventory.setItem(size - 1, mana);
        inventory.setItem(size - 9, health);

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

    public int getSlotForSkill(String skillId){
        return this.treeCache.getSlotForSkill(skillId);
    }
}
