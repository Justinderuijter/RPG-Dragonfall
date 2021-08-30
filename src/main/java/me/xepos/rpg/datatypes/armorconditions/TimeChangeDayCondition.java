package me.xepos.rpg.datatypes.armorconditions;

import me.xepos.rpg.dugcore.events.AsyncTimeChangeEvent;
import org.bukkit.event.Event;

public class TimeChangeDayCondition implements IConditionComponent{
    @Override
    public boolean isMet(Event event) {
        if (event instanceof AsyncTimeChangeEvent e && e.getCurrentDayTime() == AsyncTimeChangeEvent.TimeOfDay.DAY){
            return true;
        }
        return false;
    }
}
