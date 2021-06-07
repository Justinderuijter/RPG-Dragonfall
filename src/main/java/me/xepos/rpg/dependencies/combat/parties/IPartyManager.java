package me.xepos.rpg.dependencies.combat.parties;

import org.bukkit.entity.Player;

public interface IPartyManager {
    boolean isPlayerAllied(Player source, Player target);
}
