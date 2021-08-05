package me.xepos.rpg.datatypes;

import me.xepos.rpg.datatypes.armoreffects.IEffectComponent;
import org.bukkit.event.Event;

import java.util.List;

public class ArmorEffect {
    private final double chance;
    private final List<IEffectComponent> effects;

    public ArmorEffect(double chance, List<IEffectComponent> effects){
        this.chance = chance;
        this.effects = effects;
    }

    public void activate(Event event){
        for (IEffectComponent effect:effects) {
            effect.activate(event);
        }
    }
}
