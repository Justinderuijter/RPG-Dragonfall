package me.xepos.rpg.dependencies.combat.pvptoggle;

import org.bukkit.entity.Player;

public class DefaultPvPToggle implements IPvPToggle{
    @Override
    public boolean hasPvPEnabled(Player player) {
        return true;
    }
}
