package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.events.XRPGGainEXPEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

public class EXPListener implements Listener {
    private final XRPG plugin;

    public EXPListener(XRPG plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity().getKiller() == null) return;
        //if (e.getEntity() instanceof AbstractVillager || e.getEntity().getPersistentDataContainer().has()) return;
        Player player = e.getEntity().getKiller();
        XRPGPlayer gainer = plugin.getXRPGPlayer(player);

        if (gainer != null) {
            double health = e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            XRPGGainEXPEvent event = new XRPGGainEXPEvent(gainer, health / 2);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()){
                //TODO: permission based EXP modifier
                gainer.addExp(event.getAmount() * plugin.getConfig().getDouble("exp-multiplier", 1.0));
            }
        }
    }
}
