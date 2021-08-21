package me.xepos.rpg.dugcore;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.dugcore.events.AsyncTimeChangeEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public class TimeManager {
    private final XRPG plugin;
    private final long tickDelay;
    private final String worldName;
    private AsyncTimeChangeEvent.TimeOfDay lastTimeOfDay = null;
    private AsyncTimeChangeEvent.TimeOfDay currentTimeOfDay = null;

    public TimeManager(XRPG plugin){
        this.plugin = plugin;
        this.tickDelay = verifyTickDelay(plugin.getConfig().getLong("time-manager.tick-interval", 20));
        this.worldName = verifyWorldName(plugin.getConfig().getString("time-manager.default-world", "world"));
        innitTimeEvent();
    }

    private void innitTimeEvent(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long time = Bukkit.getServer().getWorld(worldName).getTime();

            //Values taken from minecraft wiki
            //https://minecraft.fandom.com/wiki/Daylight_cycle
            lastTimeOfDay = currentTimeOfDay;
            if (time >= 12969){
                // beginning of night
                currentTimeOfDay = AsyncTimeChangeEvent.TimeOfDay.NIGHT;
            }else{
                currentTimeOfDay = AsyncTimeChangeEvent.TimeOfDay.DAY;
            }

            if (lastTimeOfDay != currentTimeOfDay){
                Bukkit.getPluginManager().callEvent(new AsyncTimeChangeEvent(currentTimeOfDay));
            }

        },tickDelay, tickDelay);
    }

    private long verifyTickDelay(long tickDelay){
        if (tickDelay <= 0)
            return 1;

        return tickDelay;
    }

    private String verifyWorldName(String worldName){
        if (worldName == null || StringUtils.isBlank(worldName)){
            Bukkit.getLogger().severe("Default-world was null!");
            Bukkit.getLogger().severe("Attempting to use \"world\"");

            if (Bukkit.getServer().getWorld("world") == null){
                Bukkit.getLogger().severe("Could not find world!");
            }

            return "world";
        }

        return worldName;
    }

    public AsyncTimeChangeEvent.TimeOfDay getCurrentTimeOfDay(){
        return this.currentTimeOfDay;
    }
}
