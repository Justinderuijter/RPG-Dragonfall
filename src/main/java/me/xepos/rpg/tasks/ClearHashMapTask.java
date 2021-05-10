package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.datatypes.IClearable;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClearHashMapTask extends BukkitRunnable {
    private final XRPG plugin;
    private final ConcurrentHashMap<UUID, ? extends IClearable> clearableHashMap;

    public ClearHashMapTask(XRPG plugin, ConcurrentHashMap<UUID, ? extends IClearable> clearableHashMap) {
        this.plugin = plugin;
        this.clearableHashMap = clearableHashMap;
    }

    @Override
    public void run() {
        int count = 0;
        for (UUID id : clearableHashMap.keySet()) {
            if (clearableHashMap.get(id).shouldRemove()) {
                count++;
                clearableHashMap.remove(id);
            }
        }
        if (plugin.getConfig().getBoolean("Garbage Collection.Verbose", false)) {
            Bukkit.getLogger().info("[RPG] Removed " + count + " entries from memory");
        }
    }
}
