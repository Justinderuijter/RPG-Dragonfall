package me.xepos.rpg.dependencies;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class LevelledMobsManager {
    private final ConcurrentHashMap<Location, Integer> locationLevelMap;
    private final int lowerBound;
    private final int upperBound;
    private final Random random;

    public LevelledMobsManager(XRPG plugin, int lowerBound, int upperBound){
        this.locationLevelMap = new ConcurrentHashMap<>();
        this.random = new Random();
        this.lowerBound = Math.min(lowerBound, 0);
        this.upperBound = Math.max(upperBound, 0);

        Bukkit.getScheduler().runTaskTimer(plugin,() -> {
            locationLevelMap.clear();
            takeSnapshot(plugin.getRPGPlayers().values());
        }, 600L, 1200L);
    }

    public void takeSnapshot(Collection<XRPGPlayer> xrpgPlayers){
        for (XRPGPlayer xrpgPlayer:xrpgPlayers) {
            if (xrpgPlayer != null && xrpgPlayer.getPlayer() != null && xrpgPlayer.getPlayer().getGameMode() == GameMode.SURVIVAL){
                locationLevelMap.put(xrpgPlayer.getPlayer().getLocation(), xrpgPlayer.getLevel());
            }
        }
    }

    public int getLevelForLocation(Location location){
        double closestDistance = Double.MAX_VALUE;
        int level = 1;
        for (Location snapshotLocation:locationLevelMap.keySet()) {
            double distance = snapshotLocation.distanceSquared(location);
            if (distance < closestDistance){
                closestDistance = distance;
                level = locationLevelMap.get(snapshotLocation);
            }
        }

        //True = add offset level, false = subtract offset level
        if (random.nextBoolean()){
            level += random.nextInt(upperBound);
        }else{
            level -= random.nextInt(lowerBound);
        }

        return level < 0 ? 1 : level;
    }

}
