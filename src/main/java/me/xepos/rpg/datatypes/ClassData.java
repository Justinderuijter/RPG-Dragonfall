package me.xepos.rpg.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassData {
    private int level;
    private double experience;
    private int lastMana;
    private byte skillUpgradePoints;
    private byte skillUnlockPoints;

    private HashMap<String, Integer> skills;
    private List<String> keybindOrder;

    public ClassData(int level, double experience, int lastMana, byte skillUpgradePoints, byte skillUnlockPoints, HashMap<String, Integer> skills, List<String> keybindOrder) {
        this.level = level;
        this.experience = experience;
        this.lastMana = lastMana;
        this.skillUpgradePoints = skillUpgradePoints;
        this.skillUnlockPoints = skillUnlockPoints;

        this.skills = skills;
        this.keybindOrder = keybindOrder;
    }

    public ClassData() {
        this.level = 0;
        this.experience = 0;
        this.lastMana = 0;
        this.skillUpgradePoints = 0;
        this.skillUnlockPoints = 0;

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

    public HashMap<String, Integer> getSkills() {
        return skills;
    }

    public List<String> getKeybindOrder() {
        return keybindOrder;
    }
}
