package me.xepos.rpg.listeners;

import me.lokka30.levelledmobs.events.MobPreLevelEvent;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.dependencies.LevelledMobsManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class LevelledMobsListener implements Listener {
    private final XRPG plugin;
    private final LevelledMobsManager levelledMobsManager;

    public LevelledMobsListener(XRPG plugin, LevelledMobsManager levelledMobsManager) {
        this.plugin = plugin;
        this.levelledMobsManager = levelledMobsManager;
    }

    @EventHandler
    public void LMPreLevelEvent(MobPreLevelEvent e) {
        final Location entityLocation = e.getEntity().getLocation();
        int locationLevel = levelledMobsManager.getLevelForLocation(entityLocation);

        e.setLevel(locationLevel);
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent e){
        XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(e.getPlayer(), false);
        if (xrpgPlayer != null){
            levelledMobsManager.replaceLocation(xrpgPlayer);
        }
    }

}
