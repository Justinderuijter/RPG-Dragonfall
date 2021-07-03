package me.xepos.rpg.datatypes;

public class SavedSkillProperties {
    private final int level;
    private final boolean isEventSkill;

    public SavedSkillProperties(int level, boolean isEventSkill) {
        this.level = level;
        this.isEventSkill = isEventSkill;
    }

    public int getLevel() {
        return level;
    }

    public boolean isEventSkill() {
        return isEventSkill;
    }
}
