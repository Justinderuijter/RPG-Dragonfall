package me.xepos.rpg.datatypes;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tree.SkillInfo;
import me.xepos.rpg.tree.SkillTree;
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
    private final WeakReference<XRPGPlayer> xrpgPlayer;
    private final HashMap<String, Integer> skillsToUnlock;
    private final HashMap<String, XRPGSkill> skills;
    private final HashMap<String, Integer> progression;
    private final XRPG plugin;

    public TreeData(XRPGPlayer xrpgPlayer, @Nullable String currentTreeId, SkillTree currentTree, XRPG plugin) {
        if (currentTreeId != null)
            this.currentTreeId = currentTreeId;

        this.xrpgPlayer = new WeakReference<>(xrpgPlayer);
        this.currentTree = currentTree;
        this.plugin = plugin;
        this.skills = xrpgPlayer.getAllLearnedSkills();

        this.skillsToUnlock = new HashMap<>();
        this.progression = new HashMap<>();
        for (String skillId : skills.keySet()) {
            this.progression.put(skillId, 0);
        }
    }

    public boolean canLevel(String skillId){
        if (skillsToUnlock.containsKey(skillId)){
            SkillInfo skillInfo = currentTree.getSkillInfo(skillId);
            if (skillInfo == null) return false;

            if (skillsToUnlock.get(skillId) < skillInfo.getMaxLevel()){
                return true;
            }
        }else if (progression.containsKey(skillId)){
            SkillInfo skillInfo = currentTree.getSkillInfo(skillId);
            XRPGSkill xrpgSkill = skills.get(skillId);
            if (skillInfo == null || xrpgSkill == null) return false;

            if (xrpgSkill.getSkillLevel() + progression.get(skillId) < skillInfo.getMaxLevel()){
                return true;
            }
        }
        return false;
    }

    public int getCurrentSkillLevel(String skillId){
        if (skillsToUnlock.containsKey(skillId)){
            return skillsToUnlock.get(skillId);
        } else{
            XRPGSkill xrpgSkill = skills.get(skillId);
            if (xrpgSkill == null) return 0;

            if(progression.containsKey(skillId)) {

                return xrpgSkill.getSkillLevel() + progression.get(skillId);

            }else{ return xrpgSkill.getSkillLevel(); }
        }
    }

    public void revertSkill(String skillId){
        boolean removeProgression = false;
        boolean removeToUnlock = false;

        if (progression.containsKey(skillId)){
            if (progression.get(skillId) > 0){
                removeProgression = true;
            }
        } else if(skillsToUnlock.containsKey(skillId)){
            if (skillsToUnlock.get(skillId) > 0){
                removeToUnlock = true;
            }
        }

        final XRPGPlayer player = xrpgPlayer.get();
        if (player == null) return;


        if (removeProgression){
            reduceHashmapLevel(player, progression, skillId);
        }else if (removeToUnlock){
            reduceHashmapLevel(player, skillsToUnlock, skillId);
        }
    }

    private void reduceHashmapLevel(XRPGPlayer player, HashMap<String, Integer> hashMap, String skillId){
        final int level = hashMap.getOrDefault(skillId, 0);
        hashMap.put(skillId, level - 1);
        if (level - 1 <= 0){
            unLevelUnlocks(player, skillId, hashMap);
        }else {
            player.setSkillUpgradePoints(player.getSkillUpgradePoints() + 1);
        }
    }

    public void unLevelUnlocks(XRPGPlayer player, String skillId, HashMap<String, Integer> hashMap){
        hashMap.remove(skillId);
        player.setSkillUnlockPoints(player.getSkillUnlockPoints() + 1);

        for (String unlock: currentTree.getUnlocks(skillId)) {
            final Inventory inventory = player.getPlayer().getOpenInventory().getTopInventory();
            final int slot = currentTree.getSlotForSkill(unlock);


           for (int i = 0; i < getCurrentSkillLevel(unlock) + 1; i++) {
                revertSkill(unlock);
            }

/*            if (skillsToUnlock.containsKey(unlock)){
                final int level = skillsToUnlock.get(unlock);

                player.setSkillUpgradePoints(player.getSkillUpgradePoints() + level -1);
                player.setSkillUnlockPoints(player.getSkillUnlockPoints() + 1);
            }*/

            updateClickedIcon(inventory, slot, inventory.getItem(slot), getCurrentSkillLevel(unlock));
        }
    }



    public void addLevel(String skillId) {
        XRPGSkill xrpgSkill = skills.get(skillId);

        if (hasAllRequired(skillId)) { // Check if we have all required skills
            if (xrpgSkill == null) { //We don't know the skill yet
                if (skillsToUnlock.containsKey(skillId)) {
                    //if we don't have the skill but it is cached
                    final int level = skillsToUnlock.get(skillId);
                    skillsToUnlock.put(skillId, level + 1);
                } else {
                    //if we don't have the skill and it's not cached
                    skillsToUnlock.put(skillId, 1);
                }
            } else { //We did already know the skill
                final int level = progression.getOrDefault(skillId, 0);
                progression.put(skillId, level + 1);
            }
        }
    }

    public boolean hasAllRequired(String skillId){
        final SkillInfo skillInfo = currentTree.getSkillInfo(skillId);
        if (skillInfo == null) return false;

        List<String> requiredSkills = skillInfo.getRequired();

        for (String string:requiredSkills) {
            if (!skills.containsKey(string) && !skillsToUnlock.containsKey(string)) {
                xrpgPlayer.get().getPlayer().sendMessage("You do not have " + string);
                return false;
            }
        }
        return true;
    }

    public void applyChanges(SkillLoader skillLoader){
        for (String skillId:skills.keySet()) {
            final int levelsToAdd = progression.get(skillId);
            final XRPGSkill xrpgSkill = skills.get(skillId);
            xrpgSkill.setSkillLevel(xrpgSkill.getSkillLevel() + levelsToAdd);
        }

        XRPGPlayer player = xrpgPlayer.get();
        if (player != null) {
            for (String skillId : skillsToUnlock.keySet()) {
                skillLoader.addSkillToPlayer(skillId, player, skillsToUnlock.get(skillId));
            }
        }

    }

    @SuppressWarnings("ConstantConditions")
    public void updateClickedIcon(Inventory inventory, int slotId, ItemStack item, int newLevel){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().remove(plugin.getKey("level"));
        int maxLevel = itemMeta.getPersistentDataContainer().get(plugin.getKey("maxLevel"), PersistentDataType.INTEGER);
        Material material;
        if (newLevel == 0){
            material = Material.RED_WOOL;
        }else if(newLevel == maxLevel){
            material = Material.GREEN_WOOL;
        }else{
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


    public void addSkillToUnlock(String skillId, int level) {
        if (hasAllRequired(skillId)) {
            skillsToUnlock.put(skillId, level);
        }
    }

    public int getSkillToUnlockLevel(String skillId) {
        return skillsToUnlock.get(skillId);
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
