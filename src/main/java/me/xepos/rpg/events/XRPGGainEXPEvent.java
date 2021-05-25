package me.xepos.rpg.events;

import me.xepos.rpg.XRPGPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGGainEXPEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private final XRPGPlayer xrpgPlayer;
    private double amount;

    public XRPGGainEXPEvent(XRPGPlayer gainer, double amount){
        this.xrpgPlayer = gainer;
        this.amount = amount;
    }


    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public boolean isCancelled(){
        return this.isCancelled;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
