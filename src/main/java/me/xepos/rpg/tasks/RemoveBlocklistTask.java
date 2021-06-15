package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class RemoveBlocklistTask extends BukkitRunnable {
    private final Set<Location> locationList;
    private final XRPG plugin;

    public RemoveBlocklistTask(Set<Location> locationList, XRPG plugin){
        this.locationList = locationList;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Location location: locationList) {
            if (location.getWorld() != null) {
                Block block = location.getWorld().getBlockAt(location);
                //TODO: make this more flexible, currently only works for Stone Defense.
                if (block.getType() == Material.STONE)
                    location.getWorld().getBlockAt(location).setType(plugin.getTemporaryBlocks().get(location));
            }
            plugin.getTemporaryBlocks().remove(location);
        }
    }
}
