package me.xepos.rpg.dependencies.protection;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public class FactionsProtectionManager implements IProtectionManager {

    protected FactionsProtectionManager(){ }

    @Override
    public boolean isLocationValid(Location sourceLocation, @Nullable Location targetLocation) {
        Faction sourceFaction = BoardColl.get().getFactionAt(PS.valueOf(sourceLocation));

        if (!sourceFaction.getFlag(MFlag.getFlagPvp()))
            return false;

        if (targetLocation != null) {
            Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetLocation));

            if (!targetFaction.getFlag(MFlag.getFlagPvp()))
                return false;
        }

        return true;
    }

    @Override
    public boolean isPvPTypeSame(Location sourceLocation, Location targetLocation) {
        Faction sourceFaction = BoardColl.get().getFactionAt(PS.valueOf(sourceLocation));
        Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetLocation));

        if (sourceFaction.getFlag(MFlag.getFlagPvp()) == targetFaction.getFlag(MFlag.getFlagPvp())) {
            return true;
        }

        return false;
    }
}
