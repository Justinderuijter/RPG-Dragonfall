package me.xepos.rpg.listeners;

import me.lokka30.levelledmobs.events.MobPreLevelEvent;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.dependencies.LevelledMobsManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LevelledMobsListener implements Listener {
    private final LevelledMobsManager levelledMobsManager;

    public LevelledMobsListener(XRPG plugin) {
        int lowerBound = 0;
        int upperBound = 0;
        final ConfigurationSection LMSection = plugin.getConfig().getConfigurationSection("general-dependencies.levelled-mobs");
        if (LMSection != null){
            LMSection.getInt("max-negative-level-offset", 0);
            LMSection.getInt("max-positive-level-offset", 0);
        }

        this.levelledMobsManager = new LevelledMobsManager(plugin, lowerBound, upperBound);
    }

    @EventHandler
    public void LMPreLevelEvent(MobPreLevelEvent e) {
        final Location entityLocation = e.getEntity().getLocation();
        int locationLevel = levelledMobsManager.getLevelForLocation(entityLocation);

        e.setLevel(locationLevel);
    }

}
