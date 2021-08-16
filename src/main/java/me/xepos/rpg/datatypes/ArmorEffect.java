package me.xepos.rpg.datatypes;

import me.xepos.rpg.datatypes.armorconditions.ConditionType;
import me.xepos.rpg.datatypes.armorconditions.IConditionComponent;
import me.xepos.rpg.datatypes.armoreffects.IEffectComponent;
import org.bukkit.event.Event;

import java.util.List;

public class ArmorEffect {
    private final double chance;
    private final ConditionType conditionType;
    private final List<IConditionComponent> conditionComponents;
    private final List<IEffectComponent> effects;

    public ArmorEffect(double chance, ConditionType conditionType, List<IConditionComponent> conditionComponents, List<IEffectComponent> effects){
        this.conditionType = conditionType;
        this.conditionComponents = conditionComponents;
        this.chance = chance;
        this.effects = effects;
    }

    public void activate(Event event){
        boolean canProc = conditionType == ConditionType.AND ? canTriggerAND(event) : canTriggerOR(event);

        if (canProc) {
            for (IEffectComponent effect : effects) {
                effect.activate(event);
            }
        }
    }

    private boolean canTriggerAND(Event event){
        for (IConditionComponent condition:conditionComponents) {
            if (!condition.isMet(event)){
                return false;
            }
        }
        return true;
    }

    private boolean canTriggerOR(Event event){
        for (IConditionComponent condition:conditionComponents) {
            if (condition.isMet(event)){
                return true;
            }
        }
        return false;
    }
}
