package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.entity.Boss;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BossCondition implements IConditionComponent{
    @Override
    public boolean isMet(Event event) {
        if (event instanceof EntityDamageByEntityEvent e){
            return e.getEntity() instanceof Boss;
        }
        return false;
    }
}
