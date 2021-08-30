package me.xepos.rpg.events;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ArmorSet;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ArmorSetChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final XRPGPlayer xrpgPlayer;
    private final ArmorSet armorSet;
    private final byte oldLevel;
    private byte newLevel;

    public ArmorSetChangeEvent(XRPGPlayer xrpgPlayer, ArmorSet armorSet, byte oldLevel, byte newLevel){
        this.xrpgPlayer = xrpgPlayer;
        this.armorSet = armorSet;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public XRPGPlayer getXRPGPlayer() {
        return xrpgPlayer;
    }

    public ArmorSet getArmorSet() {
        return armorSet;
    }

    public byte getOldLevel() {
        return oldLevel;
    }

    public byte getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(byte newLevel) {
        this.newLevel = newLevel;
    }
}
