package me.xepos.rpg.dependencies.protection;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public class DefaultProtectionManager implements IProtectionManager {

    protected DefaultProtectionManager() {
    }

    @Override
    public boolean isLocationValid(Location sourceLocation, @Nullable Location targetLocation) {
        return true;
    }

    @Override
    public boolean isPvPTypeSame(Location sourceLocation, Location targetLocation) {
        return true;
    }
}
