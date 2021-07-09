package me.xepos.rpg.dependencies.hooks;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EssentialsXHook extends AbstractHook{
    private final static IEssentials e = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");

    public static boolean isAFK(Player player){
        return e.getUser(player).isAfk();
    }
}
