package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.entity.LivingEntity;

public class HealthCondition implements IConditionComponent{
    private final boolean isBiggerThan;
    private final double health;

    public HealthCondition(String effect){
        this.isBiggerThan = effect.contains(">");
        this.health = Integer.parseInt(effect.substring(1));
    }

    @Override
    public boolean isMet(LivingEntity livingEntity) {
        if (isBiggerThan){
            return livingEntity.getHealth() > health;
        }else{
            return livingEntity.getHealth() < health;
        }
    }
}
