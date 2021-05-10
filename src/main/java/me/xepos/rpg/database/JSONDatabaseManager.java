package me.xepos.rpg.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.ClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class JSONDatabaseManager implements IDatabaseManager {
    private final static XRPG plugin = XRPG.getPlugin(XRPG.class);
    private final static String playerFolderName = "PlayerData";

    private static File playerDataFolder;
    public final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ClassLoader classLoader;

    protected JSONDatabaseManager(ClassLoader classLoader) {
        this.classLoader = classLoader;
        File baseFile = plugin.getDataFolder();
        if (!baseFile.exists()) {
            if (baseFile.mkdir()) {
                createPlayerDataFolder(baseFile);
            }
        } else if (playerDataFolder == null || !playerDataFolder.exists()) {
            createPlayerDataFolder(baseFile);
        }
    }

    @Override
    public void loadPlayerData(UUID playerId) {
        File playerFile = new File(playerDataFolder, playerId.toString() + ".json");
        if (playerFile.exists()) {
            try {
                //Reading Json file and turning it into a JsonObject
                //so we can get specific elements
                String data = FileUtils.readFileToString(playerFile, "UTF-8");
                JsonObject jsonData = gson.fromJson(data, JsonObject.class);
                //Extract the class from JsonObject
                String classId = jsonData.get("classId").getAsString();

                XRPGPlayer xrpgPlayer = new XRPGPlayer(playerId, classId);
                classLoader.load(classId, xrpgPlayer);
                plugin.addRPGPlayer(playerId, xrpgPlayer);

            } catch (IOException ex) {
                System.out.println("Couldn't load player data for " + playerId.toString() + ".json");
            } catch (Exception ex) {
                Bukkit.getLogger().info(ex.getMessage());
                ex.printStackTrace();
            }

        } else {
            String defaultClassId = plugin.getDefaultClassId();
            XRPGPlayer xrpgPlayer = new XRPGPlayer(playerId, defaultClassId);
            classLoader.load(defaultClassId, xrpgPlayer);
            plugin.addRPGPlayer(playerId, xrpgPlayer);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void savePlayerData(XRPGPlayer xrpgPlayer) {
        String playerData = gson.toJson(xrpgPlayer);
        File test = new File(playerDataFolder, xrpgPlayer.getPlayerId().toString() + ".json");
        try {
            test.createNewFile();
            FileWriter myWriter = new FileWriter(test);
            myWriter.write(playerData);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while trying to save player data.");
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        //Json doesn't need to disconnect
    }

    private void createPlayerDataFolder(File base) {
        playerDataFolder = new File(base, playerFolderName);
        if (!playerDataFolder.mkdir()) {
            System.out.println("Could not create directory " + playerFolderName);
        }
    }
}
