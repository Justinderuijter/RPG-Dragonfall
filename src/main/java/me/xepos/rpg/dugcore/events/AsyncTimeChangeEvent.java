package me.xepos.rpg.dugcore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncTimeChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final TimeOfDay currentDayTime;

    public AsyncTimeChangeEvent(TimeOfDay timeOfDay){
        super(true);
        this.currentDayTime = timeOfDay;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum TimeOfDay{
        DAY,
        NIGHT;
    }

    public TimeOfDay getCurrentDayTime() {
        return currentDayTime;
    }
}
