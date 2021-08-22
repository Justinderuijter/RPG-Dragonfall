package me.xepos.rpg.datatypes;

import me.xepos.rpg.enums.ArmorSetTriggerType;

import java.util.EnumMap;

public class ArmorSetData {
    private byte level;
    private final EnumMap<ArmorSetTriggerType, ArmorEffect> armorEffects;

    public ArmorSetData(byte level, EnumMap<ArmorSetTriggerType, ArmorEffect> armorEffects){
        this.level = level;
        this.armorEffects = armorEffects;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public EnumMap<ArmorSetTriggerType, ArmorEffect> getArmorEffects() {
        return armorEffects;
    }
}
