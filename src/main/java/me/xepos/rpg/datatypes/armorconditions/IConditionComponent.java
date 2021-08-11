package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.entity.LivingEntity;

public interface IConditionComponent {
    boolean isMet(LivingEntity livingEntity);
}
