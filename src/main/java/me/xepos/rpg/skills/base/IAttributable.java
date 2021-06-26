package me.xepos.rpg.skills.base;

import me.xepos.rpg.datatypes.AttributeModifierData;

import java.util.List;

public interface IAttributable {
    List<AttributeModifierData> getModifiersToApply();

    void initializeAttributes(int skillLevel);
}
