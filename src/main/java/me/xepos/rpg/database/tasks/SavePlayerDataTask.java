package me.xepos.rpg.database.tasks;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.IDatabaseManager;
import org.bukkit.scheduler.BukkitRunnable;

public class SavePlayerDataTask extends BukkitRunnable {

    private final IDatabaseManager databaseManager;
    private final XRPGPlayer xrpgPlayer;

    public SavePlayerDataTask(IDatabaseManager databaseManager, XRPGPlayer xrpgPlayer) {
        this.databaseManager = databaseManager;
        this.xrpgPlayer = xrpgPlayer;
    }

    @Override
    public void run() {
        databaseManager.savePlayerData(xrpgPlayer);
    }
}
