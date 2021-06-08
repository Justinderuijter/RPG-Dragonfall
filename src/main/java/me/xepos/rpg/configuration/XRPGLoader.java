package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class XRPGLoader {
    private final XRPG plugin;
    private final File loaderFolder;
    private final String internalFolderName;

    public XRPGLoader(XRPG plugin, String subFolderName, String internalFolderName){
        this.plugin = plugin;
        this.loaderFolder = new File(plugin.getDataFolder(), subFolderName);
        this.internalFolderName = internalFolderName;
    }


    void extractAllSkillData() {
        if (!loaderFolder.exists()) {
            loaderFolder.mkdir();
        }

        try {
            List<Path> paths = getPathsFromResourceJAR(internalFolderName);
            for (Path path : paths) {
                String pathString = path.toString();
                if (pathString.startsWith("/")){
                    pathString = pathString.replaceFirst("/", "");
                }
                saveResource(pathString, false);
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
            resourcePath = resourcePath.replace(internalFolderName + "/", "");
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
            } else {
                File outFile = new File(loaderFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(loaderFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
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

    protected File getLoaderFolder() {
        return loaderFolder;
    }

    protected XRPG getPlugin() {
        return plugin;
    }
}
