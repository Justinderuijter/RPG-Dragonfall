package me.xepos.rpg.tasks;

import org.bukkit.entity.Player;

import java.util.concurrent.Callable;

public class SetHealthCallable implements Callable<Double> {
    private final Player player;
    private final double amount;

    public SetHealthCallable(Player player, double amount){
        this.player = player;
        this.amount = amount;
    }

    @Override
    public Double call() throws Exception {
        player.setHealth(amount);
        return amount;
    }
}
