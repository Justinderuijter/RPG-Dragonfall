package me.xepos.rpg.datatypes;

import java.util.HashMap;

public class TreeCache {
    private final HashMap<String, Integer> skillInSlotMap;

    public TreeCache(){
        this.skillInSlotMap = new HashMap<>();
    }

    public int getSlotForSkill(String skillId){
        return skillInSlotMap.getOrDefault(skillId, -1);
    }

    public void addToCache(String skillId, int inventorySlotIndex){
        this.skillInSlotMap.put(skillId, inventorySlotIndex);
    }
    
    public boolean contains(String skillId){
        return skillInSlotMap.containsKey(skillId);
    }
}
