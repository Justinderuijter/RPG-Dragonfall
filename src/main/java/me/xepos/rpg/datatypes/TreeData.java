package me.xepos.rpg.datatypes;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.enums.SkillRefundType;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tree.SkillInfo;
import me.xepos.rpg.tree.SkillTree;

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

    public TreeData(XRPGPlayer xrpgPlayer, @Nullable String currentTreeId, SkillTree currentTree) {
        if (currentTreeId != null)
            this.currentTreeId = currentTreeId;

        this.xrpgPlayer = new WeakReference<>(xrpgPlayer);
        this.currentTree = currentTree;
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

    public SkillRefundType revertSkill(String skillId){
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

        if (removeProgression){
            final int level = progression.getOrDefault(skillId, 0);
            progression.put(skillId, level - 1);
            if (level - 1 <= 0){
                progression.remove(skillId);
                return SkillRefundType.REFUND_UNLOCK_POINT;
            }
            return SkillRefundType.REFUND_UPGRADE_POINT;

        }else if (removeToUnlock){
            final int level = skillsToUnlock.getOrDefault(skillId, 0);
            skillsToUnlock.put(skillId, level - 1);
            if (level - 1 <= 0){
                skillsToUnlock.remove(skillId);

                return SkillRefundType.REFUND_UNLOCK_POINT;
            }
            return SkillRefundType.REFUND_UPGRADE_POINT;
        }

        return SkillRefundType.NO_REFUND;
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

/*    public XRPGTreeOutcome addLevel(String skillId, int maxLevel) {
        XRPGSkill xrpgSkill = skills.get(skillId);
        if (xrpgSkill == null) {
            return XRPGTreeOutcome.SKILL_NOT_FOUND;
        }

        if (xrpgSkill.getSkillLevel() + 1 > maxLevel) {
            return XRPGTreeOutcome.FAIL;
        }
        xrpgSkill.setSkillLevel(xrpgSkill.getSkillLevel());
        addProgression(skillId, 1);
        return XRPGTreeOutcome.SUCCESS;
    }*/

    public void addSkillToUnlock(String skillId, int level) {
        skillsToUnlock.put(skillId, level);
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
