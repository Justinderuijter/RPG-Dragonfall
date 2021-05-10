package me.xepos.rpg.dependencies.protection;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public class FactionsUUIDProtectionManager implements IProtectionManager{

    protected FactionsUUIDProtectionManager(){ }

    @SuppressWarnings("all")
    @Override
    public boolean isLocationValid(Location sourceLocation, @Nullable Location targetLocation) {
        FLocation fSourceLocation = new FLocation(sourceLocation);
        Faction sourceFaction = Board.getInstance().getFactionAt(fSourceLocation);
        if (sourceFaction.isSafeZone() || sourceFaction.isPeaceful())
            return false;

        if (targetLocation != null) {
            FLocation fTargetLocation = new FLocation(targetLocation);
            Faction targetFaction = Board.getInstance().getFactionAt(fTargetLocation);
            if (targetFaction.isSafeZone() || targetFaction.isPeaceful())
                return false;

        }

        return true;
    }

    @Override
    public boolean isPvPTypeSame(Location sourceLocation, Location targetLocation) {
        Faction sourceFaction = Board.getInstance().getFactionAt(new FLocation(sourceLocation));
        Faction targetFaction = Board.getInstance().getFactionAt(new FLocation(targetLocation));

        return sourceFaction.isSafeZone() || sourceFaction.isPeaceful() == targetFaction.isSafeZone() || targetFaction.isPeaceful();
    }
}
