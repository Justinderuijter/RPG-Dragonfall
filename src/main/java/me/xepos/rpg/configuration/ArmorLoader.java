package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.datatypes.ArmorSetData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class ArmorLoader extends XRPGLoader{
    public ArmorLoader(XRPG plugin) {
        super(plugin, "armor", "armorsets");
    }

    public HashMap<String, ArmorSetData> initialize() {
        extractAllSkillData();

        HashMap<String, ArmorSetData> configurationHashMap = new HashMap<>();

        for (File file : getLoaderFolder().listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName().replace(".yml", "");

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

            configurationHashMap.put(fileName, new ArmorSetData(fileName, fileConfiguration));
        }

        return configurationHashMap;
    }
}
