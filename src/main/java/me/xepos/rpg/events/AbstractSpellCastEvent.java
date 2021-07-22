package me.xepos.rpg.events;

import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSpellCastEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final XRPGSkill skill;
    private final SpellType[] spellTypes;
    private boolean isCancelled;

    public AbstractSpellCastEvent(XRPGSkill skill, SpellType[] spellTypes){
        this.skill = skill;
        this.spellTypes = spellTypes;
        this.isCancelled = false;
    }

    /**
     * Should not be use for comparison, only for displaying the skill name if desired.
     *
     * @return instance of the spell that was casted
     */
    public XRPGSkill getSkill() {
        return skill;
    }

    public SpellType[] getSpellTypes() {
        return spellTypes;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}
