package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.entity.LivingEntity;

public interface IModifierHolder {
    void applyModifiers(LivingEntity livingEntity);

    void removeModifiers(LivingEntity livingEntity);

    void activate(LivingEntity livingEntity);
}
