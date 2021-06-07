package me.xepos.rpg.dependencies.combat.protection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public class WorldGuardProtectionManager implements IProtectionManager{
    protected WorldGuardProtectionManager(){ }

    @Override
    public boolean isLocationValid(Location sourceLocation, @Nullable Location targetLocation) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        com.sk89q.worldedit.util.Location sourceLoc = BukkitAdapter.adapt(sourceLocation);

        ApplicableRegionSet sourceSet = query.getApplicableRegions(sourceLoc);
        for (ProtectedRegion region : sourceSet) {
            if (region.getFlag(Flags.PVP) == StateFlag.State.DENY) {
                return false;
            }
        }

        if (targetLocation != null) {
            com.sk89q.worldedit.util.Location targetLoc = BukkitAdapter.adapt(targetLocation);

            ApplicableRegionSet targetSet = query.getApplicableRegions(targetLoc);
            for (ProtectedRegion region : targetSet) {
                if (region.getFlag(Flags.PVP) == StateFlag.State.DENY) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean isPvPTypeSame(Location sourceLocation, Location targetLocation) {
        boolean sourceHasPvP = true;
        boolean targetHasPvP = true;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        com.sk89q.worldedit.util.Location sourceLoc = BukkitAdapter.adapt(sourceLocation);
        com.sk89q.worldedit.util.Location targetLoc = BukkitAdapter.adapt(targetLocation);

        ApplicableRegionSet sourceSet = query.getApplicableRegions(sourceLoc);
        for (ProtectedRegion region : sourceSet) {
            if (region.getFlag(Flags.PVP) == StateFlag.State.DENY) {
                sourceHasPvP = false;
            }
        }

        ApplicableRegionSet targetSet = query.getApplicableRegions(targetLoc);
        for (ProtectedRegion region : targetSet) {
            if (region.getFlag(Flags.PVP) == StateFlag.State.DENY) {
                targetHasPvP = false;
            }
        }

        return sourceHasPvP == targetHasPvP;
    }
}
