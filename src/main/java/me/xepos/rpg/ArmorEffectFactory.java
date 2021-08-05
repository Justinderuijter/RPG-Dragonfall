package me.xepos.rpg;

import me.xepos.rpg.datatypes.ArmorEffect;
import me.xepos.rpg.datatypes.armoreffects.*;
import me.xepos.rpg.enums.ArmorSetTriggerType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ArmorEffectFactory {
    public static HashMap<ArmorSetTriggerType, ArmorEffect> getTriggers(ConfigurationSection eventSection){
        HashMap<ArmorSetTriggerType, ArmorEffect> armorEffects = new HashMap<>();
        for (String key:eventSection.getKeys(false)) {
            ConfigurationSection eventVariables = eventSection.getConfigurationSection(key);
            if (eventVariables == null) continue;
            ArmorSetTriggerType triggerType = ArmorSetTriggerType.valueOf(key.toUpperCase());


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


            ArmorEffect armorEffect = new ArmorEffect(eventVariables.getDouble("chance", 100), effectComponents);
            armorEffects.put(triggerType, armorEffect);
        }
        return armorEffects;
    }
}
