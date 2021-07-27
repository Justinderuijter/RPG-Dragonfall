package me.xepos.rpg.utils;

import me.xepos.rpg.XRPG;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class DamageUtils {
    private final static XRPG plugin = XRPG.getInstance();

    public static double calculateSpellDamage(double rawDamage, int level, LivingEntity target){
        double toughness = 0;

        AttributeInstance instance = target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        if (instance != null){
            if (instance.getValue() > 0) {
                toughness = instance.getValue() + 8;
            }
        }


        if (target instanceof  Player) return toughness + rawDamage;

        return toughness + rawDamage * (1 + level * plugin.getSpellDamageMultiplier());
    }

    public static double calculateDamage(double rawDamage, int level, LivingEntity target){
        if (target instanceof  Player) return rawDamage;

        return rawDamage * (1 + level * plugin.getDamageMultiplier());
    }


}
