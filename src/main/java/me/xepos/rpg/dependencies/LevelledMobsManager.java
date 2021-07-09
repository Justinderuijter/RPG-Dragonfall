package me.xepos.rpg.dependencies;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.LocationInformation;
import me.xepos.rpg.dependencies.hooks.EssentialsXHook;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LevelledMobsManager {
    private final ConcurrentHashMap<UUID, LocationInformation> locationLevelMap;
    private final int lowerBound;
    private final int upperBound;
    private final Random random;
    private final boolean useEssentialsHook;

    public LevelledMobsManager(XRPG plugin, int lowerBound, int upperBound){
        this.locationLevelMap = new ConcurrentHashMap<>();
        this.random = new Random();
        this.lowerBound = lowerBound;
        this.upperBound = Math.max(upperBound, 0);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("general-dependencies");
        this.useEssentialsHook = Bukkit.getPluginManager().getPlugin("Essentials") != null && configurationSection != null && configurationSection.getBoolean("essentialsX.enable-hook", false);

        Bukkit.getScheduler().runTaskTimer(plugin,() -> {
            locationLevelMap.clear();
            takeSnapshot(plugin.getRPGPlayers().values());
            Bukkit.broadcastMessage("Cached " + locationLevelMap.size() + " locations");
        }, 600L, 1200L);
    }

    public void takeSnapshot(Collection<XRPGPlayer> xrpgPlayers){
        for (XRPGPlayer xrpgPlayer:xrpgPlayers) {
            if (xrpgPlayer != null && xrpgPlayer.getPlayer() != null){
                Player player = xrpgPlayer.getPlayer();
                if (useEssentialsHook){
                    if (EssentialsXHook.isAFK(player)) continue;
                }

                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    locationLevelMap.put(xrpgPlayer.getPlayer().getUniqueId(), new LocationInformation(xrpgPlayer.getPlayer().getLocation(), xrpgPlayer.getLevel()));
                }
            }
        }
    }

    public int getLevelForLocation(Location location){
        double closestDistance = Double.MAX_VALUE;
        int level = 1;
        for (UUID uuid:locationLevelMap.keySet()) {
            LocationInformation locationInformation = locationLevelMap.get(uuid);
            if (locationInformation == null || location.getWorld() != locationInformation.getWorld()){
                continue;
            }

            double distance = locationInformation.distanceSquared(location);
            if (distance < closestDistance){
                closestDistance = distance;
                level = locationInformation.getLevel();
            }
        }

        //True = add offset level, false = subtract offset level
        if (random.nextBoolean()){
            level += random.nextInt(upperBound);
        }else{
            level -= random.nextInt(lowerBound);
        }

        return level < 1 ? 1 : level;
    }

    public void replaceLocation(XRPGPlayer xrpgPlayer){
        this.locationLevelMap.put(xrpgPlayer.getPlayerId(), new LocationInformation(xrpgPlayer.getPlayer().getLocation(), xrpgPlayer.getLevel()));
    }

}
