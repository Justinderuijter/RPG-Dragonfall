package me.xepos.rpg.dependencies.combat.parties;

import org.bukkit.entity.Player;

public class DefaultManager implements IPartyManager {

    protected DefaultManager() {
    }

    @Override
    public boolean isPlayerAllied(Player source, Player target) {
        return true;
    }

}
