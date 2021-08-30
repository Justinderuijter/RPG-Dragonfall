package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

public class TimeCondition implements IConditionComponent{
    private int minTime;
    private int maxTime;

    public TimeCondition(String arg){
        final String[] args = arg.split(":");

        if (args.length == 1){
            if (args[0].equalsIgnoreCase("DAY")){
                this.minTime = 12969;
                this.maxTime = 24000;
            }else if(args[0].equalsIgnoreCase("NIGHT")){
                this.minTime = 1;
                this.maxTime = 12968;
            }
            return;
        }

        try{
            this.minTime = Integer.parseInt(args[0]);
        }catch(NumberFormatException ignore){
            Bukkit.getLogger().warning("Time Condition Minimum time was invalid!");
            Bukkit.getLogger().warning("Time Condition Minimum time set to 0!");
            this.minTime = 0;
        }

        try{
            this.maxTime = Integer.parseInt(args[1]);
        }catch(NumberFormatException ignore){
            Bukkit.getLogger().warning("Time Condition Maximum time was invalid!");
            Bukkit.getLogger().warning("Time Condition Maximum time set to 24000!");
            this.maxTime = 24000;
        }
    }

    @Override
    public boolean isMet(Event event) {
        if (event instanceof EntityDamageEvent e){
            final long time = e.getEntity().getWorld().getTime();
            return time >= minTime && time <= maxTime;
        } else if (event instanceof PlayerEvent e){
            final long time = e.getPlayer().getWorld().getTime();
            Bukkit.getLogger().info("min: " + minTime);
            Bukkit.getLogger().info("max: " + maxTime);
            Bukkit.getLogger().info("current: " + time);
            return time >= minTime && time <= maxTime;
        }
        return false;
    }
}
