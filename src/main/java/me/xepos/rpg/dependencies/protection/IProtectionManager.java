package me.xepos.rpg.dependencies.protection;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public interface IProtectionManager {

    boolean isLocationValid(Location sourceLocation, @Nullable Location targetLocation);

    boolean isPvPTypeSame(Location sourceLocation, Location targetLocation);
}
