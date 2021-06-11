package me.xepos.rpg.datatypes;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tree.SkillInfo;
import me.xepos.rpg.tree.SkillTree;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * A class used to cache and calculate the data of a tree edit session
 */
public class TreeData {

    private String currentTreeId;
    private SkillTree currentTree;
    private int spentUpgradePoints;
    private int spentUnlockPoints;
    private int healthProgression;
    private int manaProgression;
    private final WeakReference<XRPGPlayer> xrpgPlayer;
    private final HashMap<String, XRPGSkill> skills;
    private final HashMap<String, Integer> progression;
    private final XRPG plugin;

    public TreeData(XRPGPlayer xrpgPlayer, @Nullable String currentTreeId, SkillTree currentTree, XRPG plugin) {
        if (currentTreeId != null)
            this.currentTreeId = currentTreeId;

        this.spentUnlockPoints = 0;
        this.spentUpgradePoints = 0;
        this.healthProgression = 0;
        this.manaProgression = 0;
        this.xrpgPlayer = new WeakReference<>(xrpgPlayer);
        this.currentTree = currentTree;
        this.plugin = plugin;
        this.skills = xrpgPlayer.getAllLearnedSkills();

        this.progression = new HashMap<>();
        for (String skillId : skills.keySet()) {
            this.progression.put(skillId, 0);
        }
    }

    /**
     * Checks if a skill is max level or not
     *
     * @param skillId: The unique identifier for the skill that will be checked
     * @return true if the skill is not the maximum level, otherwise false.
     */
    public boolean isNotMaxed(String skillId) {
        XRPGSkill skill = skills.get(skillId);

        int level = progression.getOrDefault(skillId, 0);
        SkillInfo skillInfo = currentTree.getSkillInfo(skillId);
        if (skillInfo == null) return false;

        if (skill == null) {
            return level < skillInfo.getMaxLevel();
        } else {
            return skill.getSkillLevel() + level < skillInfo.getMaxLevel();
        }
    }

    /**
     * Gets the level of the skill.
     * This combines the level of the cached data and the level of the learned skill.
     *
     * @param skillId: The unique identifier for the skill that will be checked
     * @return the level of the skill
     */
    public int getCurrentSkillLevel(String skillId) {
        XRPGSkill skill = skills.get(skillId);

        if (skill == null) {
            return progression.getOrDefault(skillId, 0);
        } else {
            if (progression.containsKey(skillId)) {
                return skill.getSkillLevel() + progression.get(skillId);
            } else return skill.getSkillLevel();
        }
    }

    public int getCurrentAttributeLevel(String attributeId){
        XRPGPlayer xrpgPlayer = this.xrpgPlayer.get();
        if (xrpgPlayer != null) {
            if (attributeId.equalsIgnoreCase("health")) {
                return healthProgression + xrpgPlayer.getHealthLevel();
            } else if (attributeId.equalsIgnoreCase("mana")) {
                return manaProgression + xrpgPlayer.getManaLevel();
            }
        }
        return 0;
    }

    /**
     * Unlevels the specified skill and the skills that require this skill (if any)
     *
     * @param skillId   The unique identifier for the skill that will be unleveled
     * @param revertAll if the skill should be fully unleveled, false means only 1 level will be removed
     */
    public void revertSkill(String skillId, final boolean revertAll) {
        if (progression.containsKey(skillId) && progression.get(skillId) > 0) {

            final int level = progression.get(skillId);
            if (revertAll) {
                progression.remove(skillId);
                spentUnlockPoints--;
                spentUpgradePoints = spentUpgradePoints - level + 1;

            } else {
                progression.put(skillId, level - 1);
                if (level - 1 <= 0) {
                    progression.remove(skillId);
                    spentUnlockPoints--;
                } else {
                    spentUpgradePoints--;
                }
            }

            if (progression.getOrDefault(skillId, 0) <= 0) {
                final XRPGPlayer player = xrpgPlayer.get();
                if (player == null) return;

                final Inventory inventory = player.getPlayer().getOpenInventory().getTopInventory();

                for (String unlock : currentTree.getUnlocks(skillId)) {
                    final int slot = currentTree.getSlotForSkill(unlock);

                    if (!hasRequired(unlock, false)) {
                        revertSkill(unlock, true);
                        updateClickedIcon(inventory, slot, inventory.getItem(slot), getCurrentSkillLevel(unlock));
                    }
                }
            }
        }
    }


    /**
     * Adds a level to the specified skill if the requirements are met and it is not maxed
     *
     * @param skillId The unique identifier for the skill that will be leveled
     */
    public void addSkillLevel(String skillId) {
        if (hasRequired(skillId, false)) {
            if (isNotMaxed(skillId)) {
                if (progression.containsKey(skillId)) {
                    final int level = progression.get(skillId);
                    progression.put(skillId, level + 1);

                    spentUpgradePoints++;
                } else {
                    progression.put(skillId, 1);

                    spentUnlockPoints++;
                }
            }
        }
    }

    public void addAttributeLevel(String attributeId) {
        if (hasUpgradePoints()) {
            if (attributeId.equalsIgnoreCase("health")) {
                healthProgression++;
                spentUpgradePoints++;
            } else if (attributeId.equalsIgnoreCase("mana")) {
                manaProgression++;
                spentUpgradePoints++;
            }
        }
    }

    public void removeAttributeLevel(String attributeId) {
        if (attributeId.equalsIgnoreCase("health") && healthProgression > 0) {
            healthProgression--;
            spentUpgradePoints--;
        } else if (attributeId.equalsIgnoreCase("mana") && manaProgression > 0) {
            manaProgression--;
            spentUpgradePoints--;
        }

    }

    /**
     * Checks if the player has enough points to unlock a skill
     *
     * @return true if the player has enough points to unlock a skill, else false.
     */
    public boolean hasUnlockPoints() {
        XRPGPlayer player = xrpgPlayer.get();
        if (player == null) return false;

        if (spentUnlockPoints < 0) {
            player.getPlayer().sendMessage(ChatColor.RED + "Something went wrong:");
            player.getPlayer().sendMessage(ChatColor.RED + "spentUnlockPoints in TreeData is negative!");
            player.getPlayer().sendMessage(ChatColor.RED + "Please forward this to your administrator.");
            Bukkit.getLogger().warning(ChatColor.RED + player.getPlayer().getName() + ": spentUnlockPoints in TreeData is negative");
        }
        if (spentUnlockPoints < player.getSkillUnlockPoints()) {
            return true;
        }
        player.getPlayer().sendMessage(ChatColor.RED + "You do not have enough unlock points");
        return false;
    }

    /**
     * Checks if the player has enough points to upgrade a skill
     *
     * @return true if the player has enough points to upgrade a skill, else false.
     */
    public boolean hasUpgradePoints() {
        XRPGPlayer player = xrpgPlayer.get();
        if (player == null) return false;

        if (spentUpgradePoints < 0) {
            player.getPlayer().sendMessage(ChatColor.RED + "Something went wrong:");
            player.getPlayer().sendMessage(ChatColor.RED + "spentUpgradePoints in TreeData is negative!");
            player.getPlayer().sendMessage(ChatColor.RED + "Please forward this to your administrator.");
            Bukkit.getLogger().warning(ChatColor.RED + player.getPlayer().getName() + ": spentUpgradePoints in TreeData is negative");
        }
        if (spentUpgradePoints < player.getSkillUpgradePoints()) {
            return true;
        }
        player.getPlayer().sendMessage(ChatColor.RED + "You do not have enough upgrade points");
        return false;
    }

    /**
     * Checks if the player has the required prerequisite skill(s) to level the specified skill
     *
     * @param skillId The unique identifier for the skill that will be tested
     * @return true if the player has enough points to upgrade a skill, else false.
     */
    public boolean hasRequired(String skillId, boolean sendMessage) {
        final XRPGPlayer player = xrpgPlayer.get();
        final SkillInfo skillInfo = currentTree.getSkillInfo(skillId);
        if (skillInfo == null) return false;

        List<String> requiredSkills = skillInfo.getRequired();
        if (requiredSkills.isEmpty()) {
            return true;
        }

        for (String string : requiredSkills) {
            if (skills.containsKey(string) || progression.containsKey(string)) {
                return true;
            }
        }
        if (player != null && sendMessage) {
            player.getPlayer().sendMessage(ChatColor.RED + "You do not have the prerequisite skill required for this skill!");
        }
        return false;
    }

    /**
     * Syncs the cached data with the actual player data.
     * Learns the skills that need to be learned and levels those that need to be leveled.
     *
     * @param skillLoader The skillLoader that will be used to add skills to the player.
     */
    public void applyChanges(SkillLoader skillLoader) {
        for (String skillId : skills.keySet()) {
            final int levelsToAdd = progression.get(skillId);
            final XRPGSkill xrpgSkill = skills.get(skillId);

            final int skillLevel = xrpgSkill.getSkillLevel() + levelsToAdd;

            xrpgSkill.setSkillLevel(skillLevel);

            progression.remove(skillId);
        }

        XRPGPlayer player = xrpgPlayer.get();
        if (player != null) {
            //Skills
            for (String skillId : progression.keySet()) {
                skillLoader.addSkillToPlayer(skillId, player, progression.get(skillId));
            }
            //Health
            final int level = getCurrentAttributeLevel("health");
            player.setHealthLevel(level);
            AttributeModifierManager.getInstance().reapplyHealthAttribute(player.getPlayer(), level);

            //Mana
            player.setManaLevel(getCurrentAttributeLevel("mana"));

            player.setSkillUpgradePoints((byte) (player.getSkillUpgradePoints() - spentUpgradePoints));
            player.setSkillUnlockPoints((byte) (player.getSkillUnlockPoints() - spentUnlockPoints));
        }

    }

    /**
     * Updates the item in the specified inventory and slot to correctly display data
     * such as skill level and progression to the player.
     *
     * @param inventory the inventory that will be checked
     * @param slotId    the inventory slot that will be updated
     * @param item      the item that will be updated
     * @param newLevel  the new level that should be displayed
     */
    @SuppressWarnings("ConstantConditions")
    public void updateClickedIcon(Inventory inventory, int slotId, ItemStack item, int newLevel) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(plugin.getKey("skillId"), PersistentDataType.STRING)) {
            itemMeta.getPersistentDataContainer().remove(plugin.getKey("level"));
            int maxLevel = itemMeta.getPersistentDataContainer().get(plugin.getKey("maxLevel"), PersistentDataType.INTEGER);
            Material material;
            if (newLevel == 0) {
                material = Material.RED_WOOL;
            } else if (newLevel == maxLevel) {
                material = Material.GREEN_WOOL;
            } else {
                material = Material.ORANGE_WOOL;
            }

            String name = itemMeta.getDisplayName().substring(0, itemMeta.getDisplayName().indexOf("("));
            name += "(" + newLevel + "/" + maxLevel + ")";
            itemMeta.setDisplayName(name);

            itemMeta.getPersistentDataContainer().set(plugin.getKey("level"), PersistentDataType.INTEGER, newLevel);
            ItemStack newItem = new ItemStack(material);
            newItem.setItemMeta(itemMeta);

            inventory.setItem(slotId, newItem);
        }else if(itemMeta.getPersistentDataContainer().has(plugin.getKey("attribute"), PersistentDataType.STRING)){
            item.setAmount(newLevel + 1);
        }
    }


    private void addProgression(String skillId, int amount) {
        int levels = progression.get(skillId);
        progression.put(skillId, levels + amount);
    }

    public String getCurrentTreeId() {
        return currentTreeId;
    }

    public void setCurrentTreeId(String currentTreeId) {
        this.currentTreeId = currentTreeId;
    }

    public SkillTree getCurrentTree() {
        return currentTree;
    }

    public void setCurrentTree(SkillTree currentTree) {
        this.currentTree = currentTree;
    }
}
