package me.xepos.rpg;

import me.xepos.rpg.datatypes.ArmorSet;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;

public class ArmorManager {
    private final HashMap<String, ArmorSet> armorSets;

    public ArmorManager(){
        this.armorSets = new HashMap<>();
    }

    @Nullable
    public ArmorSet getArmorSet(String armorSetId){
        return armorSets.get(armorSetId);
    }

    public void addArmorSet(String armorSetId, ArmorSet set){
        armorSets.put(armorSetId, set);
    }

    public Set<String> getAllSetIds(){
        return this.armorSets.keySet();
    }
}
