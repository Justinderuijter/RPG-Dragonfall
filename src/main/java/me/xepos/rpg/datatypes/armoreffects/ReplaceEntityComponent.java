package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class ReplaceEntityComponent extends ReplaceEffectComponent {
    public ReplaceEntityComponent(String effect) {
        super(effect);
    }

    @Override
    public void activate(Event event) {
        if(event instanceof EntityShootBowEvent e){
            Entity entity = e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), getEntityType());
            e.setProjectile(entity);
        }else if (event instanceof EntityDeathEvent e){
            Location location = e.getEntity().getLocation();
            location.getWorld().spawnEntity(location, getEntityType());
        }
    }

}
