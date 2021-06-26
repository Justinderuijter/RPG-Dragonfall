package me.xepos.rpg.dependencies.combat.protection;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;

public interface IProtectionManager {

    boolean isLocationValid(Location sourceLocation, @Nullable Location targetLocation);

    boolean isPvPTypeSame(Location sourceLocation, Location targetLocation);
}
