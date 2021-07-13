package me.xepos.rpg.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassData {
    private final int level;
    private final double experience;
    private final int lastMana;
    private transient int baseMana;
    private int manaLevel;
    private int healthLevel;
    private double lastHealth;
    private final byte skillUpgradePoints;
    private final byte skillUnlockPoints;

    private final HashMap<String, SavedSkillProperties> skills;
    private final List<String> keybindOrder;

    public ClassData(int level, double lastHealth, double experience, int lastMana, int manaLevel, int healthLevel, byte skillUpgradePoints, byte skillUnlockPoints, HashMap<String, SavedSkillProperties> skills, List<String> keybindOrder) {
        this.level = level;
        this.experience = experience;
        this.lastMana = lastMana;
        this.manaLevel = manaLevel;
        this.healthLevel = healthLevel;
        this.lastHealth = lastHealth;
        this.skillUpgradePoints = skillUpgradePoints;
        this.skillUnlockPoints = skillUnlockPoints;

        this.skills = skills;
        this.keybindOrder = keybindOrder;
    }

    public ClassData() {
        this.level = 1;
        this.experience = 0;
        this.lastMana = -1;
        this.skillUpgradePoints = 0;
        this.skillUnlockPoints = 1;

        this.skills = new HashMap<>();
        this.keybindOrder = new ArrayList<>();
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }

    public int getLastMana() {
        return lastMana;
    }

    public byte getSkillUpgradePoints() {
        return skillUpgradePoints;
    }

    public byte getSkillUnlockPoints() {
        return skillUnlockPoints;
    }

    public HashMap<String, SavedSkillProperties> getSkills() {
        return skills;
    }

    public SavedSkillProperties getSavedSkillProperties(String skillId){
        return skills.get(skillId);
    }

    public List<String> getKeybindOrder() {
        return keybindOrder;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public void setBaseMana(int baseMana) {
        this.baseMana = baseMana;
    }

    public int getManaLevel() {
        return manaLevel;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public double getLastHealth() {
        return lastHealth;
    }

    @Override
    public String toString(){
        return  "{ClassData{Level: + " + level + " +}{Experience: " + experience + "}{Mana: " + lastMana + "}{UpgradePoints: " + skillUpgradePoints + "}{UnlockPoints: " + skillUnlockPoints + "}}";
    }
}
