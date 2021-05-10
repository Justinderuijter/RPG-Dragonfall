package me.xepos.rpg.events;

import me.xepos.rpg.enums.DamageTakenSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class XRPGDamageTakenAddedEvent extends XRPGDamageTakenModifiedEvent implements Cancellable {
    private double amount;
    private boolean isCancelled = false;

    public XRPGDamageTakenAddedEvent(Player sourcePlayer, Player targetPlayer, DamageTakenSource source, double amount) {
        super(sourcePlayer, targetPlayer, source);

        this.amount = amount;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
