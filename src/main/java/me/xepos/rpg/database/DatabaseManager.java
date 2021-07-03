package me.xepos.rpg.database;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.enums.DatabaseType;

import java.util.UUID;

public abstract class DatabaseManager {
    private DatabaseType databaseType;

    public abstract void loadPlayerData(UUID playerId);

    /**
     * Saves all savable player data
     * @param xrpgPlayer The XRPGPlayer that will be saved
     * @return The saved data, this is not guaranteed to be up to date.
     */
    public abstract PlayerData savePlayerData(XRPGPlayer xrpgPlayer);

    public abstract void disconnect();

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    protected void setDatabaseType(DatabaseType databaseType){
        this.databaseType = databaseType;
    }
}
