package me.xepos.rpg.datatypes.armorconditions;

import me.lokka30.levelledmobs.LevelInterface;
import org.bukkit.entity.LivingEntity;

public class LMCondition implements IConditionComponent{
    private final LevelInterface levelInterface;
    private final boolean isBiggerThan;
    private final int level;

    public LMCondition(LevelInterface levelInterface, String effect){
        this.levelInterface = levelInterface;
        this.isBiggerThan = effect.contains(">");
        this.level = Integer.parseInt(effect.substring(1));
    }

    @Override
    public boolean isMet(LivingEntity livingEntity) {
        final int mobLevel = levelInterface.getLevelOfMob(livingEntity);
        if (isBiggerThan){
            return mobLevel > level;
        }else{
            if (mobLevel == -1) return false;

            return mobLevel < level;
        }
    }
}
