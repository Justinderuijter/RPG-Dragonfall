package me.xepos.rpg.datatypes.armoreffects;

import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.datatypes.IModifierHolder;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class ModifierComponent implements IEffectComponent, IModifierHolder {
    private final List<AttributeModifierData> permanentModifiers;

    public ModifierComponent(String effect){
        this.permanentModifiers = new ArrayList<>();
    }

    @Override
    public List<AttributeModifierData> getPermanentModifiers() {
        return permanentModifiers;
    }

    @Override
    public void activate(Event event) {

    }
}
