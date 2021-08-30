package me.xepos.rpg;

import me.xepos.rpg.datatypes.armorconditions.*;
import me.xepos.rpg.datatypes.armoreffects.*;
import me.xepos.rpg.enums.ArmorSetTriggerType;
import org.bukkit.Bukkit;
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
                        }else if (effectType.get().equalsIgnoreCase("ZEAL")){
                            effectComponents.add(new ZealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
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
                        }else if (effectType.get().equalsIgnoreCase("ZEAL")){
                            effectComponents.add(new ZealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
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
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
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
                        }else if (effectType.get().equalsIgnoreCase("ZEAL")){
                            effectComponents.add(new ZealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
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
                        }else if (effectType.get().equalsIgnoreCase("ZEAL")){
                            effectComponents.add(new ZealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
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
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("SHADOW_STATE")){
                            effectComponents.add(new ShadowStateComponent(effect));
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
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("SHADOW_STATE")){
                            effectComponents.add(new ShadowStateComponent(effect));
                        }
                    }
                }
                case KILL_PLAYER -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("SHADOW_STATE")){
                            effectComponents.add(new ShadowStateComponent(effect));
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
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("SHADOW_STATE")){
                            effectComponents.add(new ShadowStateComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("INCREASE_DROPS")){
                            effectComponents.add(new IncreaseLootComponent(effect));
                        }
                    }
                }
                case DEATH -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("REPLACE")){
                            effectComponents.add(new ReplaceEntityComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("SHADOW_STATE")){
                            effectComponents.add(new ShadowStateComponent(effect));
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
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
                        }
                    }
                }
                case GAIN_MCMMO_EXP -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("INCREASE_MCMMO_EXP")) {
                            effectComponents.add(new IncreaseMcMMOExpComponent(effect));
                        }else if (effectType.get().equalsIgnoreCase("POTION_EFFECT")){
                            effectComponents.add(new PotionComponent(effect));
                        }else if(effectType.get().equalsIgnoreCase("HEAL")){
                            effectComponents.add(new HealComponent(effect));
                        }
                    }
                }
                case SET_BONUS_CHANGE -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("ADD_MODIFIER") || effectType.get().equalsIgnoreCase("REMOVE_MODIFIER")) {
                            effectComponents.add(new ModifierComponent(effect));
                        }
                    }
                }
                case TIME_CHANGE_DAY -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("ADD_MODIFIER") || effectType.get().equalsIgnoreCase("REMOVE_MODIFIER")) {
                            effectComponents.add(new ModifierComponent(effect));
                        }
                    }
                }
                case TIME_CHANGE_NIGHT -> {
                    Optional<String> effectType = Arrays.stream(effect.split(":")).findFirst();
                    if (effectType.isPresent()){
                        if (effectType.get().equalsIgnoreCase("ADD_MODIFIER") || effectType.get().equalsIgnoreCase("REMOVE_MODIFIER")) {
                            effectComponents.add(new ModifierComponent(effect));
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
                coreCondition = condition.substring(0, condition.indexOf(':'));
                conditionArgs =  condition.substring(condition.indexOf(':') + 1);
            }
            Bukkit.getLogger().info("Core condition: " + coreCondition);

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
                case "TIME":
                    conditionComponents.add(new TimeCondition(conditionArgs));
                    break;
                case "TIME_CHANGE_DAY":
                    conditionComponents.add(new TimeChangeDayCondition());
                    break;
                case "TIME_CHANGE_NIGHT":
                    conditionComponents.add(new TimeChangeNightCondition());
                    break;
                case "IN_WATER":
                    conditionComponents.add(new InWaterCondition(conditionArgs));
                    break;
            }
        }

        return conditionComponents;
    }
}
