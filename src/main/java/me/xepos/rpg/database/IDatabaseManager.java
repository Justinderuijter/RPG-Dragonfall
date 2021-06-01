package me.xepos.rpg.database;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.PlayerData;

import java.util.UUID;

public interface IDatabaseManager {

    void loadPlayerData(UUID playerId);

    PlayerData savePlayerData(XRPGPlayer xrpgPlayer);

    void disconnect();
}
