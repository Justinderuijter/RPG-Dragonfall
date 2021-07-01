package me.xepos.rpg.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import me.xepos.rpg.XRPG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PaperEntityListener implements Listener {
    private final XRPG plugin;

    public PaperEntityListener(XRPG plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void OnEntityRemove(EntityRemoveFromWorldEvent e){
        plugin.projectiles.remove(e.getEntity().getUniqueId());
    }
}
