package me.xepos.rpg;

import me.xepos.rpg.datatypes.ArmorEffect;
import me.xepos.rpg.datatypes.ArmorSet;
import me.xepos.rpg.enums.ArmorSetTriggerType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ArmorManager {
    private final HashMap<String, ArmorSet> armorSets;
    private final HashMap<UUID, HashMap<ArmorSetTriggerType, ArmorEffect>> playerEffects;

    public ArmorManager(){
        this.armorSets = new HashMap<>();
        this.playerEffects = new HashMap<>();
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
