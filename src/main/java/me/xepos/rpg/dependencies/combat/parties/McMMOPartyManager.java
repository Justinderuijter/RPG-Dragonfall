package me.xepos.rpg.dependencies.combat.parties;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;

public class McMMOPartyManager implements IPartyManager{
    protected McMMOPartyManager(){ }

    @Override
    public boolean isPlayerAllied(Player source, Player target) {
        if (source == target) return true;

        Party party = UserManager.getPlayer(source).getParty();

        if (party == null) return false;

        if (party.hasMember(target.getUniqueId())){
            return true;
        }else return party.getAlly() != null && party.getAlly().hasMember(target.getUniqueId());
    }
}
