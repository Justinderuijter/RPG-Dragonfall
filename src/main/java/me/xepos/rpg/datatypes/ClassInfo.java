package me.xepos.rpg.datatypes;

import org.bukkit.Material;

import java.util.List;

public class ClassInfo {
    private boolean isEnabled;
    private final String displayName;
    private final Material icon;
    private final List<String> description;
    private final List<String> allowedEventSkillIds;
    private final byte baseMana;
    private final String skillTreeId;

    public ClassInfo(String displayName, Material icon, List<String> description, List<String> allowedEventSkillIds, byte baseMana, String skillTreeId){
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
        this.allowedEventSkillIds = allowedEventSkillIds;
        this.baseMana = baseMana;
        this.skillTreeId = skillTreeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public byte getBaseMana() {
        return baseMana;
    }

    public String getSkillTreeId() {
        return skillTreeId;
    }

    public Material getIcon() {
        return icon;
    }

    public List<String> getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean canLearnEventSkill(String skillId){
        return this.allowedEventSkillIds.contains(skillId);
    }
}
