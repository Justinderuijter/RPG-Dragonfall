package me.xepos.rpg.datatypes;

import me.xepos.rpg.datatypes.armorconditions.ConditionType;
import me.xepos.rpg.datatypes.armorconditions.IConditionComponent;
import me.xepos.rpg.datatypes.armoreffects.IEffectComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArmorEffect {
    private final double chance;
    private final double cooldown;
    private final ConditionType conditionType;
    private final List<IConditionComponent> conditionComponents;
    private final List<IEffectComponent> effects;
    private final List<AttributeModifierData> permanentModifiers;

    private long lastUse;

    public ArmorEffect(double chance, double cooldown, ConditionType conditionType, List<IConditionComponent> conditionComponents, List<IEffectComponent> effects){
        this.conditionType = conditionType;
        this.conditionComponents = conditionComponents;
        this.chance = chance;
        this.cooldown = cooldown;
        this.effects = effects;
        this.lastUse = System.currentTimeMillis();
        this.permanentModifiers = new ArrayList<>();
    }

    public void activate(Event event){
        if (System.currentTimeMillis() > lastUse + cooldown * 1000) {
            Bukkit.getLogger().info("Passed cooldown check");
            if (this.chance == 100 || this.chance <= ThreadLocalRandom.current().nextDouble() * 100) {
                Bukkit.getLogger().info("Passed random check");
                boolean canProc = true;
                if (!conditionComponents.isEmpty()) {
                    canProc = conditionType == ConditionType.AND ? canTriggerAND(event) : canTriggerOR(event);
                }

                if (canProc) {
                    Bukkit.getLogger().info("Procing effects");
                    for (IEffectComponent effect : effects) {
                        effect.activate(event);
                    }
                    lastUse = System.currentTimeMillis();
                }
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
