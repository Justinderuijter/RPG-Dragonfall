package me.xepos.rpg;

import me.xepos.rpg.datatypes.armorconditions.BossCondition;
import me.xepos.rpg.datatypes.armorconditions.HealthCondition;
import me.xepos.rpg.datatypes.armorconditions.IConditionComponent;
import me.xepos.rpg.datatypes.armorconditions.LMCondition;
import me.xepos.rpg.datatypes.armoreffects.*;
import me.xepos.rpg.enums.ArmorSetTriggerType;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ArmorEffectFactory {
    private static NamespacedKey levelledKey = NamespacedKey.fromString("LevelledMobs:level");
    public static List<IEffectComponent> getEffects(ConfigurationSection eventVariables, ArmorSetTriggerType triggerType){
        List<IEffectComponent> effectComponents = new ArrayList<>();
        eventVariables.getStringList("effects").forEach(effect -> {
            switch (triggerType){
                case BOW_PLAYER -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent() ){
                        if (effectType.get().equalsIgnoreCase("INCREASE_DAMAGE")){
                            effectComponents.add(new IncreaseDamageComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case BOW_MOB -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent() ){
                        if (effectType.get().equalsIgnoreCase("INCREASE_DAMAGE")){
                            effectComponents.add(new IncreaseDamageComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case SHOOT_BOW -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("REPLACE")) {
                            effectComponents.add(new ReplaceEntityComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")) {
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case ATTACK_PLAYER -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent() ){
                        if (effectType.get().equalsIgnoreCase("INCREASE_DAMAGE")){
                            effectComponents.add(new IncreaseDamageComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case ATTACK_MOB -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent() ){
                        if (effectType.get().equalsIgnoreCase("INCREASE_DAMAGE")){
                            effectComponents.add(new IncreaseDamageComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case DEFEND_PLAYER -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent() ){
                        if (effectType.get().equalsIgnoreCase("DECREASE_DAMAGE")){
                            effectComponents.add(new IncreaseDamageComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case DEFEND_MOB -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent() ){
                        if (effectType.get().equalsIgnoreCase("DECREASE_DAMAGE")){
                            effectComponents.add(new IncreaseDamageComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case KILL_PLAYER -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case KILL_MOB -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("REPLACE")){
                            effectComponents.add(new ReplaceEntityComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
                case GAIN_EXP -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("INCREASE_EXP")) {
                            effectComponents.add(new IncreaseExpComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }
                    }
                }
            }
        });

        return effectComponents;
    }

    public static List<IConditionComponent> getConditions(ConfigurationSection eventVariables){
        List<IConditionComponent> conditionComponents = new ArrayList<>();

        for (String condition:eventVariables.getStringList("conditions")) {

            String coreCondition = condition;
            String conditionArgs = "";

            if (condition.contains(":")){
                coreCondition = condition.substring(0, condition.indexOf(':') - 1);
                conditionArgs =  condition.substring(condition.indexOf(':') + 1);
            }


            switch (coreCondition.toUpperCase()){
                case "BOSS":
                    conditionComponents.add(new BossCondition());
                    break;
                case "HEALTH":
                    conditionComponents.add(new HealthCondition(conditionArgs));
                    break;
                case "LMCondition":
                    conditionComponents.add(new LMCondition(levelledKey, conditionArgs));
                    break;
            }
        }

        return conditionComponents;
    }
}
