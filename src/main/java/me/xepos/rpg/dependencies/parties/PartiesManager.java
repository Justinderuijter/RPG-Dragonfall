package me.xepos.rpg.dependencies.parties;

import com.alessiodp.parties.api.Parties;
import org.bukkit.entity.Player;

public class PartiesManager implements IPartyManager {
    @Override
    public boolean isPlayerAllied(Player source, Player target) {
        return Parties.getApi().areInTheSameParty(source.getUniqueId(), target.getUniqueId());
    }
}
