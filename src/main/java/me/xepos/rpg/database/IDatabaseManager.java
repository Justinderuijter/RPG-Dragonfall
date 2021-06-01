package me.xepos.rpg.database;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.PlayerData;

import java.util.UUID;

public interface IDatabaseManager {

    void loadPlayerData(UUID playerId);

    /**
     * Saves all savable player data
     * @param xrpgPlayer The XRPGPlayer that will be saved
     * @return The saved data, this is not guaranteed to be up to date.
     */
    PlayerData savePlayerData(XRPGPlayer xrpgPlayer);

    void disconnect();
}
