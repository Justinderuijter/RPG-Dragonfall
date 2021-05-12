package me.xepos.rpg.configuration;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.enums.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClassLoader {
    private final XRPG plugin;
    private static final File rpgFolder = Bukkit.getServer().getPluginManager().getPlugin("ClassesOfDragonfall").getDataFolder();
    private static final File classFolder = new File(rpgFolder, "Classes");

    public ClassLoader(XRPG plugin) {
        this.plugin = plugin;
    }

    public void checkClassFolder() {

        if (!classFolder.exists()) {
            if (classFolder.mkdir()) {
                Bukkit.getLogger().info("Class folder created");
            }
        }

        try {
            if (isDirEmpty(classFolder)) {
                ArrayList<String> files = new ArrayList<String>() {{
                    add("sentinel");
                    add("explorer");
                    add("scholar");
                    //add("assassin");
                    //add("bard");
                    //add("brawler");
                    //add("guardian");
                    //add("necromancer");
                    //add("ranger");
                    //add("ravager");
                    //add("sorcerer");
                    //add("wizard");
                }};
                for (String fileName : files) {
                    Bukkit.getLogger().info(fileName + ".yml");
                    saveResource(fileName + ".yml", false);
                }
            }
        } catch (IOException exception) {
            Bukkit.getLogger().info("Failed to read class folder!");
        }

    }

    private static boolean isDirEmpty(final File directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory.toPath())) {
            return !dirStream.iterator().hasNext();
        }
    }

    public void load(String classId, XRPGPlayer xrpgPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration classConfig = plugin.getFileConfiguration(classId);
            if (classConfig == null) {
                classConfig = plugin.getFileConfiguration(plugin.getDefaultClassId());
                if (classConfig == null) return; //Default returned null too
            }

            Bukkit.getLogger().info("Classloader: " + 1);
            //Change class clears all handlers, after that we set skills
            xrpgPlayer.setShieldAllowed(classConfig.getBoolean("allow-shield", true));

            xrpgPlayer.resetClassData(classId, plugin.getFileConfiguration(classId).getString("display.name", "???"));

            Bukkit.getLogger().info("Classloader: " + 2);
            ConfigurationSection skillSection = classConfig.getConfigurationSection("skills");
            if (skillSection == null) {
                Bukkit.getLogger().info("Could not find Skills section in " + classId);
            } else {
                Bukkit.getLogger().info("Classloader: " + 3);
                for (String skillId : skillSection.getKeys(false)) {
                    ConfigurationSection skillDataSection = skillSection.getConfigurationSection(skillId);

                    if (skillDataSection != null) {
                        try {
                            Bukkit.getLogger().info("Classloader: " + 4);
                            Class<?> clazz = Class.forName("me.xepos.rpg.skills." + skillDataSection.getName());
                            Constructor<?> constructor = clazz.getConstructor(XRPGPlayer.class, ConfigurationSection.class, XRPG.class);

                            //The instance of the skill automatically assigns itself to the XRPGPlayer
                            constructor.newInstance(xrpgPlayer, skillDataSection, plugin);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Bukkit.getLogger().info("Something went wrong for " + skillDataSection.getString("name", skillDataSection.getName()));
                        }
                    }
                }
            }

            Bukkit.getLogger().info("Classloader: " + 5);
            for (String handler : xrpgPlayer.getHandlerList().keySet()) {
                xrpgPlayer.getEventHandler(handler).initialize();
            }
            Bukkit.getLogger().info("Classloader: " + 6);
        });
    }

    public HashMap<String, FileConfiguration> initializeClasses() {
        HashMap<String, FileConfiguration> configurationHashMap = new HashMap<>();

        for (File file : classFolder.listFiles()) {
            if (file.getName().contains(".yml")) {

                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
                boolean isDefault = fileConfiguration.getBoolean("default", false);
                boolean isEnabled = fileConfiguration.getBoolean("enabled", true);

                if (!isEnabled) continue; //Skip is class is disabled

                String fileName = file.getName().replace(".yml", "");

                //First enabled class tagged with default is the default class
                if (plugin.getDefaultClassId() == null || isDefault) {
                    plugin.setDefaultClassId(fileName);
                }

                registerAttributes(fileConfiguration, fileName);

                configurationHashMap.put(fileName, fileConfiguration);
            }
            Bukkit.getLogger().info("Loaded " + file.getName());
        }
        return configurationHashMap;
    }

    private void registerAttributes(FileConfiguration fileConfiguration, String fileName){

        ConfigurationSection attributeSection = fileConfiguration.getConfigurationSection("attributes");
        if (attributeSection != null) {
            AttributeModifierManager attributeModifierManager = AttributeModifierManager.getInstance();
            for (String key : attributeSection.getKeys(false)) {
                loadAttributeModifier(attributeSection, key, fileName, attributeModifierManager);
            }
        }
    }

    private void loadAttributeModifier(ConfigurationSection attributeSection, String key, String fileName, AttributeModifierManager manager){
        String keyType = key.substring(0, key.indexOf('-')).toUpperCase();

        Attribute attribute = null;
        switch (keyType){
            case "HEALTH":
                attribute = Attribute.GENERIC_MAX_HEALTH;
                break;
            case "ARMOR":
                attribute = Attribute.GENERIC_ARMOR;
                break;
            case "TOUGHNESS":
                attribute = Attribute.GENERIC_ARMOR_TOUGHNESS;
                break;
            case "MOVESPEED":
                attribute = Attribute.GENERIC_MOVEMENT_SPEED;
                break;
            case "ATTACKSPEED":
                attribute = Attribute.GENERIC_ATTACK_SPEED;
                break;
            default:
                break;
        }

        if (attribute != null) {
            if (key.endsWith("flat-bonus")) {
                String name = fileName.toUpperCase() + "_" + keyType + "_FLAT";
                double amount = attributeSection.getDouble(key);

                if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(name))
                    manager.put(ModifierType.POSITIVE, name, new AttributeModifier(UUID.randomUUID(), name, amount, AttributeModifier.Operation.ADD_NUMBER), attribute);

            } else if (key.endsWith("multiplier")) {
                String name = fileName.toUpperCase() + "_" + keyType + "_SCALING";
                double amount = attributeSection.getDouble(key) - 1;

                if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(name))
                    manager.put(ModifierType.POSITIVE, name, new AttributeModifier(UUID.randomUUID(), name, amount, AttributeModifier.Operation.MULTIPLY_SCALAR_1), attribute);
            }
        }
    }


    @SuppressWarnings("ConstantConditions")
    public List<ItemStack> initializeMenu() {
        List<ItemStack> menuItems = new ArrayList<>();

        for (String classId : plugin.getClassData().keySet()) {
            ConfigurationSection displaySettings = plugin.getFileConfiguration(classId).getConfigurationSection("display");

            if (displaySettings != null) {

                String materialString = displaySettings.getString("icon", "BARRIER");
                //Not sure why it complains about this potentially being null but here we are
                if (materialString == null) {
                    materialString = "BARRIER";
                }

                Material material = Material.getMaterial(materialString);
                List<String> description = displaySettings.getStringList("description");

                ItemStack icon = new ItemStack(material);
                ItemMeta meta = icon.getItemMeta();
                if (meta != null) {
                    meta.setLore(description);
                    meta.setDisplayName(displaySettings.getString("name", "???"));
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "classId"), PersistentDataType.STRING, classId);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    icon.setItemMeta(meta);
                }
                menuItems.add(icon);
            }

        }
        return menuItems;
    }

    private void saveResource(@NotNull String resourcePath, boolean replace) {
        if (!resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = plugin.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
            } else {
                File outFile = new File(classFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(classFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        Bukkit.getLogger().severe("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
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
