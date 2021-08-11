package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.entity.Boss;
import org.bukkit.entity.LivingEntity;

public class BossCondition implements IConditionComponent{
    @Override
    public boolean isMet(LivingEntity livingEntity) {
        return livingEntity instanceof Boss;
    }
}
