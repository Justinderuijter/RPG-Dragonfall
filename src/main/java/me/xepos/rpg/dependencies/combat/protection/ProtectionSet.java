package me.xepos.rpg.dependencies.combat.protection;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;

import java.util.List;

public class ProtectionSet {
    private final List<IProtectionManager> set;

    protected ProtectionSet(List<IProtectionManager> set)
    {
        this.set = set;
    }

    /**
     * Validates if an action can pass while keeping in mind world protection
     *
     * @param sourceLocation The location from which an ability is cast.
     * @param targetLocation The location where the ability is cast to. Can be null if target location is irrelevant.
     * @return True if it passes all rules, will return false as soon as it fails one.
     */
    public boolean isLocationValid(Location sourceLocation, @Nullable Location targetLocation) {
        for (IProtectionManager manager : set) {
            if (!manager.isLocationValid(sourceLocation, targetLocation)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPvPTypeSame(Location sourceLocation, Location targetLocation) {
        for (IProtectionManager manager : set) {
            if (!manager.isPvPTypeSame(sourceLocation, targetLocation)) {
                return false;
            }
        }
        return true;
    }
}
