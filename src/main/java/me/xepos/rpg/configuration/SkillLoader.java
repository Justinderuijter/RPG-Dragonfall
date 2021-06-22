package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ClassData;
import me.xepos.rpg.datatypes.ClassInfo;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.datatypes.SkillData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

public class SkillLoader extends XRPGLoader{

    public SkillLoader(XRPG plugin) {
        super(plugin, "skills", "skilldata");
    }

    public HashMap<String, SkillData> initializeSkills() {
        extractAllSkillData();

        final HashMap<String, SkillData> configurationHashMap = new HashMap<>();

        for (File file : getLoaderFolder().listFiles()) {
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName().replace(".yml", "");
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection levelSection = fileConfiguration.getConfigurationSection("levels");
            if (levelSection != null){
                HashMap<String, Object> staticsMap = new HashMap<>();
                HashMap<Integer, HashMap<String, Object>> levelMap = new HashMap<>();
                for (String key:levelSection.getKeys(false)) {

                    //Section for values that will be used for undefined level values;
                    if (key.equalsIgnoreCase("statics")){
                        ConfigurationSection staticSection = levelSection.getConfigurationSection(key);

                        if (staticSection == null) continue;

                        for (String staticData:staticSection.getKeys(false)) {
                            staticsMap.put(staticData, staticSection.get(staticData));
                        }

                    }else{
                        ConfigurationSection specificLevelSection = levelSection.getConfigurationSection(key);
                        if (specificLevelSection == null) continue;

                        HashMap<String, Object> dataMap = new HashMap<>();

                        for (String skillData:specificLevelSection.getKeys(false)) {
                            dataMap.put(skillData, specificLevelSection.get(skillData));
                        }

                        levelMap.put(Integer.valueOf(key), dataMap);
                    }

                }
                configurationHashMap.put(fileName, new SkillData(fileConfiguration.getString("name"), fileConfiguration.getString("icon"), staticsMap, levelMap));
            }
        }

        return configurationHashMap;
    }

    public void loadPlayerSkills(PlayerData data, XRPGPlayer xrpgPlayer) {
        if (StringUtils.isBlank(data.getClassId())) return;

        ClassInfo classInfo = getPlugin().getClassInfo(data.getClassId());

        if (classInfo == null) return;
        //If the data exists but is null we add base classdata object

        ClassData classData = data.getClasses().get(data.getClassId());

        if (classData == null){
            //data.addClassData(data.getClassId(), new ClassData());
            classData = new ClassData();
        }

        classData.setBaseMana(getPlugin().getClassInfo(data.getClassId()).getBaseMana());

        data.getClasses().put(data.getClassId(), classData);

        String displayName = classInfo.getDisplayName();

        //data needs to hold the data for the class you're about to become
        if (xrpgPlayer.getPlayer() != null) {
            xrpgPlayer.resetPlayerDataForClassChange(data, displayName);
        }


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
            if (getPlugin().getSkillData(skillId) == null) {
                xrpgPlayer.getPlayer().sendMessage("SKILLDATA IS NULL FOR " + skillId);
                return;
            }

            constructor.newInstance(xrpgPlayer, getPlugin().getSkillData(skillId), getPlugin(), level);

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Something went wrong for " + skillId);
        }
    }
}
