package me.xepos.rpg.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOListener implements Listener {
    private final XRPG plugin;

    public McMMOListener(XRPG plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMcMMOEXPGain(McMMOPlayerXpGainEvent e){
        XRPGPlayer gainer = plugin.getXRPGPlayer(e.getPlayer());

        Bukkit.getLogger().info("Exp gained");
        if (gainer != null){
            gainer.getPlayer().sendMessage("Gained " + e.getRawXpGained() + " EXP");
            gainer.addExp(e.getRawXpGained() * plugin.getConfig().getDouble("exp-multiplier", 1.0));
        }
    }
}
