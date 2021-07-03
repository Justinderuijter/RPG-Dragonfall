package me.xepos.rpg.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.enums.DatabaseType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class JSONDatabaseManager extends DatabaseManager {
    private final static XRPG plugin = XRPG.getPlugin(XRPG.class);
    private final static String playerFolderName = "playerdata";

    private static File playerDataFolder;
    public final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final SkillLoader skillLoader;

    protected JSONDatabaseManager(SkillLoader skillLoader) {
        setDatabaseType(DatabaseType.JSON);
        this.skillLoader = skillLoader;
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

                PlayerData playerData = gson.fromJson(data, PlayerData.class);

                XRPGPlayer xrpgPlayer = new XRPGPlayer(playerId, playerData);

                skillLoader.loadPlayerSkills(playerData, xrpgPlayer);

                plugin.addRPGPlayer(playerId, xrpgPlayer);



            } catch (IOException ex) {
                System.out.println("Couldn't load player data for " + playerId.toString() + ".json");
            } catch (Exception ex) {
                Bukkit.getLogger().info(ex.getMessage());
                ex.printStackTrace();
            }

        } else {
            PlayerData data = new PlayerData("");
            XRPGPlayer xrpgPlayer = new XRPGPlayer(playerId, data);
            skillLoader.loadPlayerSkills(data, xrpgPlayer);
            plugin.addRPGPlayer(playerId, xrpgPlayer);
        }
    }

    @Override
    @SuppressWarnings("all")
    public PlayerData savePlayerData(XRPGPlayer xrpgPlayer) {
        PlayerData extractedData = xrpgPlayer.extractData();
        File dataFile = new File(playerDataFolder, xrpgPlayer.getPlayerId().toString() + ".json");
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }

            String data = FileUtils.readFileToString(dataFile, "UTF-8");
            FileWriter saveWriter = new FileWriter(dataFile);
            String dataToSave;
            PlayerData dataToReturn;

            if (StringUtils.isNotBlank(data)) {
                PlayerData savedData = gson.fromJson(data, PlayerData.class);

                //This looks confusing so to clear it up:
                //1. We take the saved data
                //2. We get the current class' class data from the extracted data
                //3. We replace the value in the savedData if it exists, otherwise add it.
                final String classId = extractedData.getClassId();
                savedData.setClassId(classId);

                if (StringUtils.isNotBlank(classId)) {
                    savedData.addClassData(classId, extractedData.getClassData(classId));
                }

                //4. turn the new data to json and save it.
                dataToSave = gson.toJson(savedData);

                dataToReturn = savedData;
            } else {
                //Just save the extracted data if nothing exists.
                dataToSave = gson.toJson(extractedData);

                dataToReturn = extractedData;
            }
            saveWriter.write(dataToSave);
            saveWriter.close();

            return dataToReturn;

        } catch (IOException e) {
            System.out.println("An error occurred while trying to save player data.");
            e.printStackTrace();
        }

        return extractedData;
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
