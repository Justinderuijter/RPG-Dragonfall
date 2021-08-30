package me.xepos.rpg.datatypes.armorconditions;

import me.xepos.rpg.datatypes.armoreffects.EffectTarget;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

public class InWaterCondition implements IConditionComponent{

    private final EffectTarget target;

    public InWaterCondition(String condition){
        final String victim = "%victim%";
        String[] args = condition.split(":");

        EffectTarget target = EffectTarget.ATTACKER;

        if (args.length > 1){
            if (args[1].equalsIgnoreCase(victim)){
                target = EffectTarget.VICTIM;
            }
        }

        this.target = target;
    }

    @Override
    public boolean isMet(Event event) {
        if (event instanceof EntityDamageByEntityEvent e){
            if (target == EffectTarget.ATTACKER){
                return e.getDamager().isInWater();
            }else{
                return e.getEntity().isInWater();
            }
        }else if (event instanceof EntityDamageEvent e){
            return e.getEntity().isInWater();
        }else if (event instanceof PlayerEvent e){
            return e.getPlayer().isInWater();
        }
        return false;
    }
}
