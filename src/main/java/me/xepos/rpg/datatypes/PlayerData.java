package me.xepos.rpg.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {
    private String classId;
    private long lastClassChange = 0;
    private int skillUpgradePoints;
    private int skillUnlockPoints;
    private final List<String> keybinds;
    private final HashMap<String, Integer> skills;

    public PlayerData(String classId, int skillUnlockPoints, int skillUpgradePoints, long lastClassChange, List<String> keybinds, HashMap<String, Integer> skills){
        this.classId = classId;
        this.skillUpgradePoints = skillUpgradePoints;
        this.skillUnlockPoints = skillUnlockPoints;
        this.lastClassChange = lastClassChange;
        this.keybinds = keybinds;
        this.skills = skills;
    }

    public PlayerData(String classId, int skillUnlockPoints, int skillUpgradePoints, long lastClassChange){
        this.classId = classId;
        this.skillUnlockPoints = skillUnlockPoints;
        this.skillUpgradePoints = skillUpgradePoints;
        this.lastClassChange = lastClassChange;
        this.keybinds = new ArrayList<>();
        this.skills = new HashMap<>();
    }

    public void setClassId(String classId){
        this.classId = classId;
    }

    public String getClassId() {
        return classId;
    }

    public long getLastClassChange() {
        return lastClassChange;
    }

    public int getSkillUnlockPoints() {
        return skillUnlockPoints;
    }

    public List<String> getKeybinds() {
        return keybinds;
    }

    public HashMap<String, Integer> getSkills() {
        return skills;
    }

    public int getSkillUpgradePoints() {
        return skillUpgradePoints;
    }
}
