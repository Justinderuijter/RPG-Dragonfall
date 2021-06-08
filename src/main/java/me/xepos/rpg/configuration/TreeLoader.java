package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.tree.SkillTree;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class TreeLoader extends XRPGLoader {

    public TreeLoader(XRPG plugin) {
        super(plugin, "trees", "treedata");
    }

    public HashMap<String, SkillTree> initialize() {
        extractAllSkillData();

        HashMap<String, SkillTree> configurationHashMap = new HashMap<>();

        for (File file : getLoaderFolder().listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName().replace(".yml", "");

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            String treeName = fileConfiguration.getString("name", fileName);

            configurationHashMap.put(fileName, new SkillTree(treeName, fileConfiguration, getPlugin()));


        }

        return configurationHashMap;
    }
}
