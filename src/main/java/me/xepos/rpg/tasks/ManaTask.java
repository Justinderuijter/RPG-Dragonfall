package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPGPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ManaTask extends BukkitRunnable {
    private final ConcurrentHashMap<UUID, XRPGPlayer> players;

    public ManaTask(ConcurrentHashMap<UUID, XRPGPlayer> players, int mana) {
        this.players = players;
    }


    @Override
    public void run() {
        for (UUID uuid : players.keySet()) {
            players.get(uuid).addMana(5);
        }
    }
}
