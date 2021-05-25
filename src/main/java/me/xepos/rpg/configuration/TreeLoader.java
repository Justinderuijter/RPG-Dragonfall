package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class TreeLoader {
    private final XRPG plugin;
    private final File treeFolder;

    public TreeLoader(XRPG plugin){
        this.plugin = plugin;
        this.treeFolder = new File(plugin.getDataFolder(), "trees");
    }

    public HashMap<String, FileConfiguration> initialize(){
        HashMap<String, FileConfiguration> configurationHashMap = new HashMap<>();

        for (File file:treeFolder.listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName().replace(".yml", "");
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

            configurationHashMap.put(fileName, fileConfiguration);
        }

        return configurationHashMap;
    }


}
