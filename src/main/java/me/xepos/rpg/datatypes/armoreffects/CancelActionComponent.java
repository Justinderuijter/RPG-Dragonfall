package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class CancelActionComponent implements IEffectComponent{
    @Override
    public void activate(Event event) {
        if (event instanceof Cancellable cancellable){
            cancellable.setCancelled(true);
        }
    }
}
