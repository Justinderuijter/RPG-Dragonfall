package me.xepos.rpg.datatypes;

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

public class TreeData {

    private String currentTreeId;
    private SkillTree currentTree;
    private int spentUpgradePoints;
    private int spentUnlockPoints;
    private final WeakReference<XRPGPlayer> xrpgPlayer;
    private final HashMap<String, XRPGSkill> skills;
    private final HashMap<String, Integer> progression;
    private final XRPG plugin;

    public TreeData(XRPGPlayer xrpgPlayer, @Nullable String currentTreeId, SkillTree currentTree, XRPG plugin) {
        if (currentTreeId != null)
            this.currentTreeId = currentTreeId;

        this.spentUnlockPoints = 0;
        this.spentUpgradePoints = 0;
        this.xrpgPlayer = new WeakReference<>(xrpgPlayer);
        this.currentTree = currentTree;
        this.plugin = plugin;
        this.skills = xrpgPlayer.getAllLearnedSkills();

        this.progression = new HashMap<>();
        for (String skillId : skills.keySet()) {
            this.progression.put(skillId, 0);
        }
    }

    public boolean canLevel(String skillId) {
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

    public void revertSkill(String skillId, final boolean revertAll) {
        if (progression.containsKey(skillId) && progression.get(skillId) > 0){

            final XRPGPlayer player = xrpgPlayer.get();
            if (player == null) return;

            final int level = progression.get(skillId);
            if (revertAll) {
                progression.remove(skillId);
                spentUnlockPoints--;
                spentUpgradePoints = spentUpgradePoints - level + 1;

                player.getPlayer().sendMessage("1:");
                player.getPlayer().sendMessage("Removed 1 unlock point.");
                player.getPlayer().sendMessage("Removed " + (level + 1) + " upgrade points");

            } else {
                progression.put(skillId, level - 1);
                if (level - 1 <= 0){
                    progression.remove(skillId);
                    spentUnlockPoints--;
                    player.getPlayer().sendMessage("2:");
                    player.getPlayer().sendMessage("Removed 1 unlock point.");
                }else {
                    spentUpgradePoints--;
                    player.getPlayer().sendMessage("3:");
                    player.getPlayer().sendMessage("Removed 1 upgrade point.");
                }
            }

            if (progression.getOrDefault(skillId, 0) <= 0){
                for (String unlock:currentTree.getUnlocks(skillId)) {
                    final Inventory inventory = player.getPlayer().getOpenInventory().getTopInventory();
                    final int slot = currentTree.getSlotForSkill(unlock);

                    revertSkill(unlock, true);
                    updateClickedIcon(inventory, slot, inventory.getItem(slot), getCurrentSkillLevel(unlock));
                }
            }


        }
    }



    public void addLevel(String skillId) {
        if (hasRequired(skillId)){
            if (canLevel(skillId)){
                if (progression.containsKey(skillId)){
                    final int level = progression.get(skillId);
                    progression.put(skillId, level + 1);

                    spentUpgradePoints++;
                }else{
                    progression.put(skillId, 1);

                    spentUnlockPoints++;
                }
            }
        }
        xrpgPlayer.get().getPlayer().sendMessage("Unlock points used: " + spentUnlockPoints);
        xrpgPlayer.get().getPlayer().sendMessage("Upgrade points used: " + spentUpgradePoints);
    }

    public boolean hasUnlockPoints() {
        XRPGPlayer player = xrpgPlayer.get();
        if (player == null) return false;

        if (spentUnlockPoints < 0) {
            player.getPlayer().sendMessage(ChatColor.RED + "Something went wrong:");
            player.getPlayer().sendMessage(ChatColor.RED + "spentUnlockPoints in TreeData is negative!");
            player.getPlayer().sendMessage(ChatColor.RED + "Please forward this to your administrator.");
            Bukkit.getLogger().warning(ChatColor.RED + player.getPlayer().getName() + ": spentUnlockPoints in TreeData is negative");
        }
        return spentUnlockPoints < player.getSkillUnlockPoints();
    }

    public boolean hasUpgradePoints() {
        XRPGPlayer player = xrpgPlayer.get();
        if (player == null) return false;

        if (spentUpgradePoints < 0) {
            player.getPlayer().sendMessage(ChatColor.RED + "Something went wrong:");
            player.getPlayer().sendMessage(ChatColor.RED + "spentUpgradePoints in TreeData is negative!");
            player.getPlayer().sendMessage(ChatColor.RED + "Please forward this to your administrator.");
            Bukkit.getLogger().warning(ChatColor.RED + player.getPlayer().getName() + ": spentUpgradePoints in TreeData is negative");
        }
        return spentUpgradePoints < player.getSkillUpgradePoints();
    }

    public boolean hasRequired(String skillId) {
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
        return false;
    }

    public void applyChanges(SkillLoader skillLoader) {
        for (String skillId : skills.keySet()) {
            final int levelsToAdd = progression.get(skillId);
            final XRPGSkill xrpgSkill = skills.get(skillId);

            xrpgSkill.setSkillLevel(xrpgSkill.getSkillLevel() + levelsToAdd);
            progression.remove(skillId);
        }

        XRPGPlayer player = xrpgPlayer.get();
        if (player != null) {
            for (String skillId : progression.keySet()) {
                skillLoader.addSkillToPlayer(skillId, player, progression.get(skillId));
            }
            player.setSkillUpgradePoints(player.getSkillUpgradePoints() - spentUpgradePoints);
            player.setSkillUnlockPoints(player.getSkillUnlockPoints() - spentUnlockPoints);
        }

    }

    @SuppressWarnings("ConstantConditions")
    public void updateClickedIcon(Inventory inventory, int slotId, ItemStack item, int newLevel) {
        ItemMeta itemMeta = item.getItemMeta();
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
    }


/*    public void addSkillToUnlock(String skillId, int level) {
        if (hasRequired(skillId)) {
            skillsToUnlock.put(skillId, level);
            spentUnlockPoints++;

            xrpgPlayer.get().getPlayer().sendMessage("Used Unlock point");
            xrpgPlayer.get().getPlayer().sendMessage("Used: " + spentUnlockPoints);
        }
    }*/

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
