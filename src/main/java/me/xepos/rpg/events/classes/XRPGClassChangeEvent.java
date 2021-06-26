package me.xepos.rpg.events.classes;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGClassChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final CommandSender commandSender;
    private final String oldClassId;
    private final String oldClassDisplayName;
    private  String newClassId;
    private final String newClassDisplayName;

    public XRPGClassChangeEvent(Player player, String oldClassId, String oldClassDisplayName, String newClassId, String newClassDisplayName) {
        this.player = player;
        this.commandSender = null;
        this.oldClassId = oldClassId;
        this.oldClassDisplayName = oldClassDisplayName;
        this.newClassId = newClassId;
        this.newClassDisplayName = newClassDisplayName;
    }


    //Getters

    public Player getPlayer() {
        return this.player;
    }

    public CommandSender getCommandSender() {
        return this.commandSender;
    }

    public String getOldClassId() {
        return oldClassId;
    }

    public String getOldClassDisplayName() {
        return oldClassDisplayName;
    }

    public String getNewClassId() {
        return newClassId;
    }

    public String getNewClassDisplayName() {
        return newClassDisplayName;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setNewClassId(String newClassId) {
        this.newClassId = newClassId;
    }
}