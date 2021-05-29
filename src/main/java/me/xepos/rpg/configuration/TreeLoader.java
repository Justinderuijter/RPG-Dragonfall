package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.tree.SkillTree;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

    public HashMap<String, SkillTree> initialize() {
        extractAllSkillData();

        HashMap<String, SkillTree> configurationHashMap = new HashMap<>();

        for (File file : treeFolder.listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName().replace(".yml", "");

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            String treeName = fileConfiguration.getString("name", fileName);

            configurationHashMap.put(fileName, new SkillTree(treeName, fileConfiguration, plugin));


        }

        return configurationHashMap;
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
