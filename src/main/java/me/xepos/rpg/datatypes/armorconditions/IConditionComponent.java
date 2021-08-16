package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.event.Event;

public interface IConditionComponent {
    boolean isMet(Event event);
}
