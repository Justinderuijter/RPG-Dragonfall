package me.xepos.rpg.listeners;

import me.lokka30.levelledmobs.events.MobPreLevelEvent;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.dependencies.LevelledMobsManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LevelledMobsListener implements Listener {
    private final LevelledMobsManager levelledMobsManager;

    public LevelledMobsListener(XRPG plugin) {
        this.levelledMobsManager = new LevelledMobsManager(plugin);
    }

    @EventHandler
    public void LMPreLevelEvent(MobPreLevelEvent e) {
        final Location entityLocation = e.getEntity().getLocation();
        int locationLevel = levelledMobsManager.getLevelForLocation(entityLocation);

        e.setLevel(locationLevel);

    }

}
