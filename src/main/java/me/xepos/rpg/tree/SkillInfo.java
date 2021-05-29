package me.xepos.rpg.tree;

import java.util.ArrayList;
import java.util.List;

public class SkillInfo {
    private List<String> required;
    private List<String> unlocks;
    private int maxLevel;

    public SkillInfo(List<String> required, List<String> unlocks, int maxLevel){
        this.required = required;
        this.unlocks = unlocks;
        this.maxLevel = maxLevel;
    }

    public SkillInfo(int maxLevel){
        this.required = new ArrayList<>();
        this.unlocks = new ArrayList<>();
        this.maxLevel = maxLevel;
    }

    public SkillInfo(){
        this.required = new ArrayList<>();
        this.unlocks = new ArrayList<>();
        this.maxLevel = 1;
    }

    public List<String> getRequired() {
        return required;
    }

    public List<String> getUnlocks() {
        return unlocks;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public void setUnlocks(List<String> unlocks) {
        this.unlocks = unlocks;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
}
