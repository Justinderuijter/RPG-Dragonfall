package me.xepos.rpg.dependencies.protection;

import org.bukkit.Location;

public class TownyAdvancedProtectionManager implements IProtectionManager{
    @Override
    public boolean isLocationValid(Location sourceLocation, Location targetLocation) {
       return true;
    }

    @Override
    public boolean isPvPTypeSame(Location sourceLocation, Location targetLocation) {
        return true;
    }
}
