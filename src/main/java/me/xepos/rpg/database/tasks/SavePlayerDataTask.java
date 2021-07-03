package me.xepos.rpg.database.tasks;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.database.DatabaseManager;
import org.bukkit.scheduler.BukkitRunnable;

public class SavePlayerDataTask extends BukkitRunnable {

    private final DatabaseManager databaseManager;
    private final XRPGPlayer xrpgPlayer;

    public SavePlayerDataTask(DatabaseManager databaseManager, XRPGPlayer xrpgPlayer) {
        this.databaseManager = databaseManager;
        this.xrpgPlayer = xrpgPlayer;
    }

    @Override
    public void run() {
        databaseManager.savePlayerData(xrpgPlayer);
    }
}
