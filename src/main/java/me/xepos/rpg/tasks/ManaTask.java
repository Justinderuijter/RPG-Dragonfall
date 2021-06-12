package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPGPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ManaTask extends BukkitRunnable {
    private final ConcurrentHashMap<UUID, XRPGPlayer> players;
    private final int mana;

    public ManaTask(ConcurrentHashMap<UUID, XRPGPlayer> players, int mana) {
        this.players = players;
        this.mana = mana;
    }


    @Override
    public void run() {
        for (UUID uuid : players.keySet()) {
            XRPGPlayer player = players.get(uuid);
            if (player == null || player.getPlayer() == null) continue;
            player.addMana(mana);
            player.sendActionBarMessage();
        }
    }
}
