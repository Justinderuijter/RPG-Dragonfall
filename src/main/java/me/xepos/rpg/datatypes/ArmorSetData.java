package me.xepos.rpg.datatypes;

import me.xepos.rpg.datatypes.armoreffects.IEffectComponent;
import me.xepos.rpg.datatypes.armoreffects.IModifierHolder;
import me.xepos.rpg.enums.ArmorSetTriggerType;
import org.bukkit.entity.Player;

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

    public ArmorSetData(ArmorSetData data){
        this.level = data.getLevel();
        this.armorEffects = new EnumMap<>(data.getArmorEffects());
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

    //Call when changing tier level
    public void removeArmorModifiers(Player player){
        ArmorEffect effect = armorEffects.get(ArmorSetTriggerType.SET_BONUS_CHANGE);
        if (effect == null) return;
        for (IEffectComponent effectComponent:effect.getEffectComponents()) {
            if (effectComponent instanceof IModifierHolder holder){
                holder.removeModifiers(player);
            }
        }
    }
}
