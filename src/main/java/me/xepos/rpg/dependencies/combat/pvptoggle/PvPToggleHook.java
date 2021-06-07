package me.xepos.rpg.dependencies.combat.pvptoggle;

import com.github.aasmus.pvptoggle.PvPToggle;
import org.bukkit.entity.Player;

public class PvPToggleHook implements IPvPToggle{

    @Override
    public boolean hasPvPEnabled(Player player) {
        return !PvPToggle.instance.players.get(player.getUniqueId());
    }
}
