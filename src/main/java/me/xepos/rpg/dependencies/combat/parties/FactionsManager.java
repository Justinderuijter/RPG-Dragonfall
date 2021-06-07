package me.xepos.rpg.dependencies.combat.parties;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.entity.Player;

public class FactionsManager implements IPartyManager {

    protected FactionsManager() {
    }

    @Override
    public boolean isPlayerAllied(Player source, Player target) {
        Faction sourceFaction = MPlayer.get(source).getFaction();
        Faction targetFaction = MPlayer.get(target).getFaction();

        if (sourceFaction == targetFaction)
            return true;

        Rel relation = sourceFaction.getRelationTo(targetFaction);

        //Returns true if factions are truced or allied, else false;
        return relation.isAtLeast(Rel.TRUCE);
    }

/*    @Override
    public boolean isPlayerTargetable(Location sourceLocation, Location targetLocation) {
        Faction sourceFaction = BoardColl.get().getFactionAt(PS.valueOf(sourceLocation));

        if (!sourceFaction.getFlag(MFlag.getFlagPvp()))
            return false;

        Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetLocation));

        if (!targetFaction.getFlag(MFlag.getFlagPvp()))
            return false;

        return true;
    }*/
}
