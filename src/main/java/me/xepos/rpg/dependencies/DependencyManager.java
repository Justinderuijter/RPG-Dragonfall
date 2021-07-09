package me.xepos.rpg.dependencies;

import me.xepos.rpg.dependencies.hooks.AbstractHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class DependencyManager {
    private final HashMap<String, AbstractHook> hooks;

    public DependencyManager(){
        this.hooks = new HashMap<>();
    }

    public static boolean isPluginEnabled(String pluginName){
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) return false;
        if (!plugin.isEnabled()) return false;

        return true;
    }

    public AbstractHook getHook(String pluginName){
        return hooks.get(pluginName);
    }

    public boolean addHook(String pluginName, AbstractHook hook){
        if (!hooks.containsKey(pluginName)){
            hooks.put(pluginName, hook);
            return true;
        }
        return false;
    }
}
