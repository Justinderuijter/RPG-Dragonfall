package me.xepos.rpg.dependencies.parties;

import org.bukkit.entity.Player;

public interface IPartyManager {
    boolean isPlayerAllied(Player source, Player target);
}
