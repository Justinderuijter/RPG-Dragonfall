package me.xepos.rpg.datatypes.armorconditions;

import me.lokka30.levelledmobs.LevelInterface;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
    public boolean isMet(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return false;

        final int mobLevel = levelInterface.getLevelOfMob((LivingEntity) e.getEntity());
        if (isBiggerThan){
            return mobLevel > level;
        }else{
            if (mobLevel == -1) return false;

            return mobLevel < level;
        }
    }
}
