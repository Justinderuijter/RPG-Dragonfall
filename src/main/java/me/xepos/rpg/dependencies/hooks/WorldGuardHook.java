package me.xepos.rpg.dependencies.hooks;

import com.sk89q.worldguard.WorldGuard;

public class WorldGuardHook extends AbstractHook{
    private static final WorldGuard WG = WorldGuard.getInstance();

/*    public boolean isInRegion(org.bukkit.Location location, String name){
        RegionQuery query = WG.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);

        for (ProtectedRegion region: query.getApplicableRegions(loc)) {

        }
    }*/
}
