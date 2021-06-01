package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ClassData;
import me.xepos.rpg.datatypes.ClassInfo;
import me.xepos.rpg.datatypes.PlayerData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Constructor;
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

public class SkillLoader {
    private final XRPG plugin;
    private static final File rpgFolder = Bukkit.getServer().getPluginManager().getPlugin("ClassesOfDragonfall").getDataFolder();
    private static final File skillFolder = new File(rpgFolder, "skills");

    public SkillLoader(XRPG plugin) {
        this.plugin = plugin;
    }

    public HashMap<String, FileConfiguration> initializeSkills() {
        extractAllSkillData();

        HashMap<String, FileConfiguration> configurationHashMap = new HashMap<>();

        for (File file : skillFolder.listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName().replace(".yml", "");
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

            configurationHashMap.put(fileName, fileConfiguration);
        }

        return configurationHashMap;
    }

    private void extractAllSkillData() {
        if (!skillFolder.exists()) {
            skillFolder.mkdir();
        }

        try {
            List<Path> paths = getPathsFromResourceJAR("skilldata");
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
            resourcePath = resourcePath.replace("skilldata/", "");
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
            } else {
                File outFile = new File(skillFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(skillFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
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

    public void loadPlayerSkills(PlayerData data, XRPGPlayer xrpgPlayer) {
        if (StringUtils.isBlank(data.getClassId())) return;

        ClassInfo classInfo = plugin.getClassInfo(data.getClassId());

        if (classInfo == null) return;
        //If the data exists but is null we add base classdata object

        ClassData classData = data.getClasses().get(data.getClassId());

        if (classData == null){
            //data.addClassData(data.getClassId(), new ClassData());
            classData = new ClassData();
        }

        String displayName = classInfo.getDisplayName();

        //data needs to hold the data for the class you're about to become
        xrpgPlayer.resetPlayerDataForClassChange(data, displayName);


        for (String skillId : classData.getSkills().keySet()) {
            final int level = data.getClassData(data.getClassId()).getSkills().getOrDefault(skillId, 1);
            addSkillToPlayer(skillId, xrpgPlayer, level);
        }

    }

    public void addSkillToPlayer(String skillId, XRPGPlayer xrpgPlayer, int level) {
        try {
            Class<?> clazz = Class.forName("me.xepos.rpg.skills." + skillId);
            Constructor<?> constructor = clazz.getConstructor(XRPGPlayer.class, ConfigurationSection.class, XRPG.class, int.class);

            //The instance of the skill automatically assigns itself to the XRPGPlayer
            if (plugin.getSkillData(skillId) == null) {
                xrpgPlayer.getPlayer().sendMessage("SKILLDATA IS NULL FOR " + skillId);
                return;
            }

            constructor.newInstance(xrpgPlayer, plugin.getSkillData(skillId), plugin, level);

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Something went wrong for " + skillId);
        }
    }
}
