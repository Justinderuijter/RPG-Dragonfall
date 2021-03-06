package me.xepos.rpg.listeners;

import me.xepos.rpg.PlayerManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.events.XRPGGainEXPEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EXPListener implements Listener {
    private final PlayerManager playerManager;
    private final FileConfiguration config;

    public EXPListener(XRPG plugin){
        this.config = plugin.getConfig();
        this.playerManager = plugin.getPlayerManager();
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity().getKiller() == null) return;
        if (e.getEntity() instanceof AbstractVillager) return;
        Player player = e.getEntity().getKiller();
        XRPGPlayer gainer = playerManager.getXRPGPlayer(player, true);

        if (gainer != null && gainer.canGainEXP()) {
            double health = e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            XRPGGainEXPEvent event = new XRPGGainEXPEvent(gainer, health / 2);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()){
                //TODO: permission based EXP modifier
                gainer.addExp(event.getAmount() * config.getDouble("exp-multiplier", 1.0));
            }
        }
    }
}
