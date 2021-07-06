package me.xepos.rpg;

import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.enums.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class AttributeModifierManager {
    private static AttributeModifierManager instance;
    public final static String HEALTH_LEVEL_MODIFIER_NAME = XRPG.modifierPrefix + "HEALTH_LEVEL";

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

    public void reapplyHealthAttribute(Player player, int level){
        removeHealthLevelModifier(player);

        if (level > 0) {
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), HEALTH_LEVEL_MODIFIER_NAME, level * 2, AttributeModifier.Operation.ADD_NUMBER);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(modifier);
            Bukkit.getLogger().info("Added " + HEALTH_LEVEL_MODIFIER_NAME + ", value: " + modifier.getAmount());
        }
    }

    public void removeHealthLevelModifier(Player player){
        Collection<AttributeModifier> modifierCollection = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers();
        for (AttributeModifier modifier:modifierCollection) {
            if (modifier.getName().equalsIgnoreCase(HEALTH_LEVEL_MODIFIER_NAME)){
                Bukkit.getLogger().info("Removed " + HEALTH_LEVEL_MODIFIER_NAME + ", value: " + modifier.getAmount());
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(modifier);
            }
        }
    }

    public void removeAllXRPGModifiers(Player player){
        for (Attribute attribute:Attribute.values()) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null){
                for (AttributeModifier modifier:instance.getModifiers()) {
                    if (modifier.getName().startsWith(XRPG.modifierPrefix)){
                        instance.removeModifier(modifier);
                    }
                }
            }
        }
    }


}
