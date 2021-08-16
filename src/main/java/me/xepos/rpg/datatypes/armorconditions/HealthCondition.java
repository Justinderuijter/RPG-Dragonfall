package me.xepos.rpg.datatypes.armorconditions;

import me.xepos.rpg.datatypes.armoreffects.EffectTarget;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;

public class HealthCondition implements IConditionComponent{
    private final boolean isBiggerThan;
    private final double healthThreshold;
    private final boolean isPercentage;
    private EffectTarget target;

    public HealthCondition(String arg){
        String[] args = arg.split(":");
        String healthArg = args[0];
        this.isBiggerThan = healthArg.contains(">");

        healthArg = healthArg.replace(">", "").replace("<", "");
        this.isPercentage = healthArg.contains("%");

        this.healthThreshold = Integer.parseInt(healthArg.replace("%", ""));

        try{
            this.target = EffectTarget.valueOf(args[1].replaceAll("%", "").toUpperCase());
        }catch (IllegalArgumentException ignore){
            this.target = EffectTarget.VICTIM;
        }

    }

    @Override
    public boolean isMet(Event event) {
        if (event instanceof EntityDamageByEntityEvent e){
            return shouldTrigger(getTarget(e));
        }else if (event instanceof PlayerEvent e){
            return shouldTrigger(e.getPlayer());
        }
        return false;
    }

    private boolean shouldTrigger(LivingEntity livingEntity){
        double health = (livingEntity.getHealth() / livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) * 100;
        if (isPercentage){
            if (isBiggerThan){
                return health > healthThreshold;
            }else{
                return health < healthThreshold;
            }

        }else{
            if (isBiggerThan){
                return livingEntity.getHealth() > healthThreshold;
            }else{
                return livingEntity.getHealth() < healthThreshold;
            }
        }
    }

    private LivingEntity getTarget(EntityDamageByEntityEvent e){
        return target == EffectTarget.VICTIM ? (LivingEntity) e.getEntity() : (LivingEntity) e.getDamager();
    }
}
