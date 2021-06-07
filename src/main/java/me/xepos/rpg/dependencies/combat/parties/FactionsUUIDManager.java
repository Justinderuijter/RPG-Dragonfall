
package me.xepos.rpg.dependencies.combat.parties;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.entity.Player;

public class FactionsUUIDManager implements IPartyManager {

    protected FactionsUUIDManager() {
    }

    @Override
    public boolean isPlayerAllied(Player source, Player target) {
        Faction sourceFaction = FPlayers.getInstance().getByPlayer(source).getFaction();
        Faction targetFaction = FPlayers.getInstance().getByPlayer(target).getFaction();

        if (sourceFaction == targetFaction)
            return true;

        Relation relation = sourceFaction.getRelationTo(targetFaction);

        //Returns true if factions are truced or allied, else false;
        return relation.isAlly() || relation.isTruce();
    }
}

