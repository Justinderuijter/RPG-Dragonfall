package me.xepos.rpg.datatypes;

import me.xepos.rpg.datatypes.armorconditions.IConditionComponent;
import me.xepos.rpg.datatypes.armoreffects.IEffectComponent;
import org.bukkit.event.Event;

import java.util.List;

public class ArmorEffect {
    private final double chance;
    private final List<IConditionComponent> conditionComponents;
    private final List<IEffectComponent> effects;

    public ArmorEffect(double chance, List<IConditionComponent> conditionComponents, List<IEffectComponent> effects){
        this.conditionComponents = conditionComponents;
        this.chance = chance;
        this.effects = effects;
    }

    public void activate(Event event){
        for (IEffectComponent effect:effects) {
            effect.activate(event);
        }
    }

    private boolean canTriggerAND(){
        for (IConditionComponent condition:conditionComponents) {
            if (!condition.isMet()){
                return false;
            }
        }
        return true;
    }

    private boolean canTriggerOR(){
        for (IConditionComponent condition:conditionComponents) {
            if (condition.isMet()){
                return true;
            }
        }
        return false;
    }
}
