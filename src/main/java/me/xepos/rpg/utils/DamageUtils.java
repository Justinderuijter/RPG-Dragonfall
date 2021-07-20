package me.xepos.rpg.utils;

import me.xepos.rpg.XRPG;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class DamageUtils {
    private final static XRPG plugin = XRPG.getInstance();

    public static double calculateSpellDamage(double rawDamage, int level, LivingEntity target){
        if (target instanceof  Player) return rawDamage;

        return rawDamage * (1 + level * plugin.getSpellDamageMultiplier());
    }

    public static double calculateDamage(double rawDamage, int level, LivingEntity target){
        if (target instanceof  Player) return rawDamage;

        return rawDamage * (1 + level * plugin.getDamageMultiplier());
    }


}
