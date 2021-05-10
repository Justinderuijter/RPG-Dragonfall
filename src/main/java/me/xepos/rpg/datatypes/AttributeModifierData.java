package me.xepos.rpg.datatypes;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

public class AttributeModifierData {
    private final Attribute attribute;
    private final AttributeModifier attributeModifier;

    public AttributeModifierData(AttributeModifier modifier, Attribute appliedAttribute) {
        this.attributeModifier = modifier;
        this.attribute = appliedAttribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public AttributeModifier getAttributeModifier() {
        return attributeModifier;
    }
}
