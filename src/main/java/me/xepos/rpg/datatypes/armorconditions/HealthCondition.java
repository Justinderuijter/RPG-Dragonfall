package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;

public class HealthCondition implements IConditionComponent{
    private final boolean isBiggerThan;
    private final double health;

    public HealthCondition(String effect){
        this.isBiggerThan = effect.contains(">");
        this.health = Integer.parseInt(effect.substring(1));
    }

    @Override
    public boolean isMet(Event event) {
        //TODO: clean up and add parameter support
        if (event instanceof EntityDamageByEntityEvent e){
            if (isBiggerThan){
                return ((LivingEntity)e.getEntity()).getHealth() > health;
            }else{
                return ((LivingEntity)e.getEntity()).getHealth() < health;
            }
        }else if (event instanceof PlayerEvent e){
            if (isBiggerThan){
                return e.getPlayer().getHealth() > health;
            }else{
                return e.getPlayer().getHealth() < health;
            }
        }
        return false;
    }
}
