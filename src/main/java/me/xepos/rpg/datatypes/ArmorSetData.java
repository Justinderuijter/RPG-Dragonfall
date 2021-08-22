package me.xepos.rpg.datatypes;

import me.xepos.rpg.enums.ArmorSetTriggerType;

import java.util.EnumMap;

public class ArmorSetData {
    private byte level;
    private EnumMap<ArmorSetTriggerType, ArmorEffect> armorEffects;

    public ArmorSetData(byte level, EnumMap<ArmorSetTriggerType, ArmorEffect> armorEffects){
        this.level = level;
        this.armorEffects = armorEffects;
    }

    public ArmorSetData(byte level){
        this.level = level;
        this.armorEffects = new EnumMap<>(ArmorSetTriggerType.class);
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

    public void setArmorEffects(EnumMap<ArmorSetTriggerType, ArmorEffect> effects) {
        this.armorEffects = effects;
    }
}
