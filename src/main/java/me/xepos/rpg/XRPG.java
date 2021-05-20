package me.xepos.rpg;

import me.xepos.rpg.commands.*;
import me.xepos.rpg.configuration.ClassLoader;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.database.DatabaseManagerFactory;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.datatypes.BaseProjectileData;
import me.xepos.rpg.dependencies.parties.IPartyManager;
import me.xepos.rpg.dependencies.parties.PartyManagerFactory;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import me.xepos.rpg.dependencies.protection.ProtectionSetFactory;
import me.xepos.rpg.listeners.EntityListener;
import me.xepos.rpg.listeners.InventoryListener;
import me.xepos.rpg.listeners.PlayerListener;
import me.xepos.rpg.listeners.ProjectileListener;
import me.xepos.rpg.tasks.ClearHashMapTask;
import me.xepos.rpg.tasks.ManaTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public final class XRPG extends JavaPlugin {

    private Inventory inventoryGUI;
    private ClassLoader classLoader;

    //Ability targetting managers
    private IPartyManager partyManager;
    private ProtectionSet protectionSet;

    //Data manager
    private IDatabaseManager databaseManager;

    //Classes
    private String defaultClassId = null;
    private static HashMap<String, FileConfiguration> classData;

    //Skills
    private static HashMap<String, FileConfiguration> skillData;

    //Players
    private static final ConcurrentHashMap<UUID, XRPGPlayer> RPGPlayers = new ConcurrentHashMap<>();

    //Custom projectiles
    public final ConcurrentHashMap<UUID, BaseProjectileData> projectiles = new ConcurrentHashMap<>();

    //Keys
    private static final HashMap<String, NamespacedKey> keyRegistry = new HashMap<>();

    @Override // Plugin startup logic
    public void onEnable() {
        //Load classes
        this.saveDefaultConfig();

        this.skillData = new SkillLoader(this).initializeSkills();
        this.classLoader = new ClassLoader(this);
        this.classLoader.checkClassFolder();
        this.classData = this.classLoader.initializeClasses();

        final String[] keyNames = new String[]{"tag", "separator", "classId", "skillId"};

        for (String name:keyNames) {
            this.keyRegistry.put(name, new NamespacedKey(this, name));
        }


        //Load database
        this.databaseManager = DatabaseManagerFactory.getDatabaseManager(classLoader);

        //Load ability targetting managers
        this.partyManager = PartyManagerFactory.getPartyManager();
        this.protectionSet = ProtectionSetFactory.getProtectionRules();

        //Prevents throwing error if databaseManager shuts down this plugin.
        if (!this.isEnabled())
            return;

        //CraftLoader disabled as it won't be used (for now)
        //new CraftLoader(this).initCustomRecipes();

        initClassChangeGUI();
        //registering listeners/commands
        initEventListeners();

        this.getCommand("xrpgdebug").setExecutor(new XRPGDebug(this, classData));
        this.getCommand("xrpgreload").setExecutor(new XRPGReload());
        this.getCommand("changeclass").setExecutor(new ChangeClassCommand(this, inventoryGUI));
        this.getCommand("spellmode").setExecutor(new ToggleSpellCommand(this));
        this.getCommand("spellbook").setExecutor(new SpellbookCommand(this));
        System.out.println("RPG classes loaded!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            databaseManager.loadPlayerData(player.getUniqueId());
        }

        int timer = this.getConfig().getInt("Garbage Collection.Timer", 120);
        if (timer > 0)
            new ClearHashMapTask(this, projectiles).runTaskTimerAsynchronously(this, timer * 20L, timer * 20L);

        if (useMana()) {
            long delay = (long) (this.getConfig().getDouble("mana.recovery-amount", 5.0) * 20);
            new ManaTask(RPGPlayers, this.getConfig().getInt("mana.recovery-amount")).runTaskTimerAsynchronously(this, delay, delay);
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (UUID uuid : RPGPlayers.keySet()) {
            XRPGPlayer xrpgPlayer = RPGPlayers.get(uuid);
            Utils.removeAllModifiers(xrpgPlayer.getPlayer());
            this.databaseManager.savePlayerData(xrpgPlayer);
        }

        this.databaseManager.disconnect();
    }

    private void initClassChangeGUI() {
        inventoryGUI = Bukkit.createInventory(null, 18, "Pick A Class");

        for (ItemStack item : classLoader.initializeMenu()) {
            int slot = Utils.getLastAvailableInventorySlot(inventoryGUI);
            if (slot != -1) {
                inventoryGUI.setItem(slot, item);
            }
        }
    }

    private void initEventListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, databaseManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this, classLoader, databaseManager), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }

    private void loadConfigs() {
        this.saveDefaultConfig();
    }

    public ProtectionSet getProtectionSet() {
        return protectionSet;
    }

    public IPartyManager getPartyManager() {
        return partyManager;
    }

    public Inventory getInventoryGUI() {
        return inventoryGUI;
    }

    public XRPGPlayer getXRPGPlayer(Player player) {
        return RPGPlayers.get(player.getUniqueId());
    }

    public XRPGPlayer getXRPGPlayer(UUID playerUUID) {
        return RPGPlayers.get(playerUUID);
    }

    public void removeXRPGPlayer(Player player) {
        RPGPlayers.remove(player.getUniqueId());
    }

    public void removeXRPGPlayer(UUID playerUUID) {
        RPGPlayers.remove(playerUUID);
    }

    public ConcurrentHashMap<UUID, XRPGPlayer> getRPGPlayers() {
        return RPGPlayers;
    }

    public void addRPGPlayer(UUID playerUUID, XRPGPlayer xrpgPlayer) {
        RPGPlayers.put(playerUUID, xrpgPlayer);
    }

    public String getDefaultClassId() {
        return defaultClassId;
    }

    public void setDefaultClassId(String defaultClassId) {
        this.defaultClassId = defaultClassId;
    }

    public void addClassData(String classId, FileConfiguration dataFile) {
        if (!this.classData.containsKey(classId)) {
            this.classData.put(classId, dataFile);
        }
    }

    public FileConfiguration getFileConfiguration(String classId) {
        return classData.get(classId);
    }

    public HashMap<String, FileConfiguration> getClassData() {
        return classData;
    }

    public NamespacedKey getKey(String keyName){
        return keyRegistry.get(keyName);
    }

    public boolean useMana() {
        return this.getConfig().getBoolean("mana.enabled", false);
    }

    public FileConfiguration getSkillData(String skillId){
        return skillData.get(skillId);
    }

    public Set<String> getAllSkills(){
        return skillData.keySet();
    }
}