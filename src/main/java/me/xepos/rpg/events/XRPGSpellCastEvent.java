package me.xepos.rpg.events;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGSpellCastEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private final XRPGPlayer player;
    private final XRPGSkill spellName;
    private final SpellType[] spellTypes;

    public XRPGSpellCastEvent(XRPGPlayer player, XRPGSkill spellName, SpellType[] spellTypes) {
        this.player = player;
        this.spellName = spellName;
        this.spellTypes = spellTypes;
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
    public void setCancelled(boolean b) {
        isCancelled = b;
    }

    public XRPGPlayer getXRPGPlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    /**
     * Should not be use for comparison, only for displaying the skill name if desired.
     *
     * @return the localized name of the cast spell
     */
    public XRPGSkill getSkill() {
        return spellName;
    }

    public SpellType[] getSpellTypes() {
        return spellTypes;
    }
}
