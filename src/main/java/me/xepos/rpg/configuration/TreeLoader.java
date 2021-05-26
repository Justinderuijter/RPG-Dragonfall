package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TreeLoader {
    private final XRPG plugin;
    private final File treeFolder;

    public TreeLoader(XRPG plugin) {
        this.plugin = plugin;
        this.treeFolder = new File(plugin.getDataFolder(), "trees");
    }

    public HashMap<String, FileConfiguration> initialize() {
        extractAllSkillData();

        HashMap<String, FileConfiguration> configurationHashMap = new HashMap<>();

        for (File file : treeFolder.listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName().replace(".yml", "");
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

            configurationHashMap.put(fileName, fileConfiguration);
        }

        return configurationHashMap;
    }

    public void buildTree(Inventory inventory, XRPGPlayer xrpgPlayer, String id) {
        HashMap<String, Integer> skills = xrpgPlayer.getAllLearnedSkills();

        FileConfiguration fileConfiguration = plugin.getTreeData(id);
        List<String> layout = fileConfiguration.getStringList("interface.order");
        int rowNum = 0;
        for (String row : layout) {
            char[] chars = row.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == 'x') continue;

                String skillId = fileConfiguration.getString("interface.legend." + chars[i] + ".skill");
                ConfigurationSection skillData = plugin.getSkillData(skillId);

                Material material = Material.RED_WOOL;
                if (skills.containsKey(skillId)) {
                    material = Material.GREEN_WOOL;
                }

                ItemStack item = Utils.buildItemStack(material, skillData.getString("name", "???"), skillData.getStringList("description"));

                setRequiredMeta(fileConfiguration, item, skillId);

                inventory.setItem(i + (rowNum * 9), item);

            }
            rowNum++;
        }
    }

    private void setRequiredMeta(FileConfiguration fileConfiguration, ItemStack item, String skillId) {
        ItemMeta meta = item.getItemMeta();


        meta.getPersistentDataContainer().set(plugin.getKey("skillId"), PersistentDataType.STRING, skillId);

        List<String> requiredSkills = fileConfiguration.getStringList("skills." + skillId + ".requires");
        if (requiredSkills.size() > 0) {

            StringBuilder requiredSkillsBuilder = new StringBuilder();
            for (String requiredSkill : requiredSkills) {
                requiredSkillsBuilder.append(requiredSkill).append(", ");
            }

            requiredSkillsBuilder.delete(requiredSkillsBuilder.length() - 1, requiredSkillsBuilder.length());

            meta.getPersistentDataContainer().set(plugin.getKey("requires"), PersistentDataType.STRING, requiredSkillsBuilder.toString());
        }

        item.setItemMeta(meta);
    }

    private void extractAllSkillData() {
        if (!treeFolder.exists()) {
            treeFolder.mkdir();
        }

        try {
            List<Path> paths = getPathsFromResourceJAR("treedata");
            for (Path path : paths) {
                saveResource(path.toString(), false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private List<Path> getPathsFromResourceJAR(String folder)
            throws URISyntaxException, IOException {

        List<Path> result;

        // get path of the current running JAR
        String jarPath = getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
        System.out.println("JAR Path :" + jarPath);

        // file walks JAR
        URI uri = URI.create("jar:file:" + jarPath);
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            result = Files.walk(fs.getPath(folder))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        return result;

    }

    private void saveResource(@NotNull String resourcePath, boolean replace) {
        if (!resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            Bukkit.getLogger().info(resourcePath);
            InputStream in = plugin.getResource(resourcePath);
            resourcePath = resourcePath.replace("treedata/", "");
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
            } else {
                File outFile = new File(treeFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(treeFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (!outFile.exists() || replace) {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException var10) {
                    Bukkit.getLogger().severe("Could not save " + outFile.getName() + " to " + outFile);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }

}
