package me.xepos.rpg.database;

import me.xepos.rpg.XRPGPlayer;

import java.util.UUID;

public interface IDatabaseManager {

    void loadPlayerData(UUID playerId);

    void savePlayerData(XRPGPlayer xrpgPlayer);

    void disconnect();
}
