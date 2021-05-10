package me.xepos.rpg;

import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.enums.ModifierType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import java.util.HashMap;

public class AttributeModifierManager {
    private static AttributeModifierManager instance;

    private final HashMap<String, AttributeModifierData> positiveModifiers = new HashMap<>();
    private final HashMap<String, AttributeModifierData> negativeModifiers = new HashMap<>();

    public HashMap<String, AttributeModifierData> getModifiers(ModifierType modifierType) {
        if (modifierType == ModifierType.NEGATIVE) {
            return negativeModifiers;
        } else {
            return positiveModifiers;
        }
    }

    public void put(ModifierType modifierType, String identifier, AttributeModifier modifier, Attribute attribute) {
        if (modifierType == ModifierType.NEGATIVE) {
            if (!negativeModifiers.containsKey(identifier))
                negativeModifiers.put(identifier, new AttributeModifierData(modifier, attribute));
        } else {
            if (!positiveModifiers.containsKey(identifier))
                positiveModifiers.put(identifier, new AttributeModifierData(modifier, attribute));
        }
    }

    public static AttributeModifierManager getInstance() {
        if (instance == null)
            instance = new AttributeModifierManager();

        return instance;
    }

    public AttributeModifierData get(ModifierType modifierType, String identifier) {
        if (modifierType == ModifierType.NEGATIVE) {
            return negativeModifiers.get(identifier);
        } else {
            return positiveModifiers.get(identifier);
        }
    }


}
