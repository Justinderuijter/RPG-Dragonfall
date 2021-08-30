package me.xepos.rpg.datatypes.armorconditions;

import me.xepos.rpg.dugcore.events.AsyncTimeChangeEvent;
import org.bukkit.event.Event;

public class TimeChangeNightCondition implements IConditionComponent{
    @Override
    public boolean isMet(Event event) {
        if (event instanceof AsyncTimeChangeEvent e && e.getCurrentDayTime() == AsyncTimeChangeEvent.TimeOfDay.NIGHT){
            return true;
        }
        return false;
    }
}
