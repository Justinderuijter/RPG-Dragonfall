package me.xepos.rpg;

import me.xepos.rpg.commands.*;
import me.xepos.rpg.configuration.ArmorLoader;
import me.xepos.rpg.configuration.ClassLoader;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.configuration.TreeLoader;
import me.xepos.rpg.database.DatabaseManager;
import me.xepos.rpg.database.DatabaseManagerFactory;
import me.xepos.rpg.datatypes.*;
import me.xepos.rpg.dependencies.DependencyManager;
import me.xepos.rpg.dependencies.LevelledMobsManager;
import me.xepos.rpg.dependencies.combat.parties.PartyManagerFactory;
import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSetFactory;
import me.xepos.rpg.dependencies.combat.pvptoggle.IPvPToggle;
import me.xepos.rpg.dependencies.combat.pvptoggle.PvPToggleFactory;
import me.xepos.rpg.dependencies.hooks.AEnchantsHook;
import me.xepos.rpg.dugcore.TimeManager;
import me.xepos.rpg.listeners.*;
import me.xepos.rpg.tasks.ClearHashMapTask;
import me.xepos.rpg.tasks.ManaTask;
import me.xepos.rpg.tasks.RemoveBlocklistTask;
import me.xepos.rpg.tree.SkillTree;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public final class XRPG extends JavaPlugin {
    private static XRPG instance;
    public static final String permissionPrefix = "xrpg.";
    public static final String modifierPrefix = "XRPG_";

    private double damageMultiplier;
    private double spellDamageMultiplier;

    private List<ItemStack> GUIBaseItems;
    private SkillLoader skillLoader;
    private TreeLoader treeLoader;

    //Ability targetting managers
    private PartySet partySet;
    private ProtectionSet protectionSet;
    private IPvPToggle pvpToggle;

    //Data manager
    private DatabaseManager databaseManager;

    //
    private DependencyManager dependencyManager;
    private TimeManager timeManager;

    //Classes
    private static HashMap<String, ClassInfo> classInfo;
    private static ClassChangeManager classChangeManager;

    //Skills
    private static HashMap<String, SkillData> skillData;

    //Skill trees
    private static HashMap<String, SkillTree> treeData;

    //Players
    private static PlayerManager playerManager;

    //Armor
    private static ArmorManager armorManager;

    //Tree viewer
    private static HashMap<UUID, TreeData> treeView;
    //Custom projectiles
    public final ConcurrentHashMap<UUID, BaseProjectileData> projectiles = new ConcurrentHashMap<>();
    //
    private final HashMap<Location, Material> temporaryBlocks = new HashMap<>();

    //Keys
    private static final HashMap<String, NamespacedKey> keyRegistry = new HashMap<>();

    @Override // Plugin startup logic
    public void onEnable() {
        instance = this;

        //Load classes
        this.saveDefaultConfig();

        //Loaders
        this.skillLoader = new SkillLoader(this);
        this.skillData = this.skillLoader.initializeSkills();
        this.treeLoader = new TreeLoader(this);
        this.treeData = treeLoader.initialize();
        this.classInfo = new ClassLoader(this).initializeClasses();

        this.treeView = new HashMap<>();


        final String[] keyNames = new String[]{"tag", "separator", "classId", "skillId", "spellbook", "requires", "level", "maxLevel", "attribute", "set"};

        for (String name:keyNames) {
            this.keyRegistry.put(name, new NamespacedKey(this, name));
        }


        //Load database
        this.playerManager = new PlayerManager(this);
        this.databaseManager = DatabaseManagerFactory.getDatabaseManager(skillLoader);
        this.dependencyManager = new DependencyManager();
        this.timeManager = new TimeManager(this);

        ConfigurationSection featureSection = getConfig().getConfigurationSection("extra-features");

        this.armorManager = new ArmorManager();


        for (Map.Entry<String, ArmorSet> entry :new ArmorLoader(this).initialize().entrySet()) {
            armorManager.addArmorSet(entry.getKey(), entry.getValue());
        }

        //Prevents throwing error if databaseManager shuts down this plugin.
        if (!this.isEnabled()) return;

        Plugin mcMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
        Plugin pvpToggle = Bukkit.getPluginManager().getPlugin("PvPToggle");

        ConfigurationSection dependencyConfig = this.getConfig().getConfigurationSection("general-dependencies");

        boolean useMcMMO = false;
        boolean usePvPToggle = false;

        if (mcMMO != null && dependencyConfig.getBoolean("mcmmo.enable-hook", false)) useMcMMO = true;
        if (pvpToggle != null && dependencyConfig.getBoolean("pvp-toggle.enable-hook", false)) usePvPToggle = true;

        //Load ability targetting managers
        this.partySet = new PartySet(PvPToggleFactory.getPvPToggle(usePvPToggle), PartyManagerFactory.getPartyManager());
        this.protectionSet = ProtectionSetFactory.getProtectionRules();
        this.classChangeManager = new ClassChangeManager(this, this.databaseManager, this.skillLoader);

        this.GUIBaseItems = generateBaseGUIItems();
        //registering listeners/commands
        initEventListeners(dependencyConfig, featureSection);

        if (useMcMMO){
            Bukkit.getLogger().info("Using mcMMO for EXP calculations.");
            getServer().getPluginManager().registerEvents(new McMMOListener(this), this);
        }else{
            getServer().getPluginManager().registerEvents(new EXPListener(this), this);
        }

        Plugin AE = Bukkit.getPluginManager().getPlugin("AdvancedEnchantments");
        Plugin LM = Bukkit.getPluginManager().getPlugin("LevelledMobs");
        if (AE != null && AE.isEnabled()){
            this.dependencyManager.addHook("AdvancedEnchantments", new AEnchantsHook());
        }
        if (LM != null && LM.isEnabled()){

        }

        this.pvpToggle = PvPToggleFactory.getPvPToggle(usePvPToggle);

        this.damageMultiplier = getConfig().getDouble("level-damage-multiplier", 0.021);
        this.spellDamageMultiplier = getConfig().getDouble("level-spell-damage-multiplier", 0.042);

        this.getCommand("xrpgdebug").setExecutor(new XRPGDebug(this));
        this.getCommand("xrpgreload").setExecutor(new XRPGReload(this, skillLoader));
        this.getCommand("spellmode").setExecutor(new ToggleSpellCommand(this));
        this.getCommand("spellbook").setExecutor(new SpellbookCommand(this));
        this.getCommand("tree").setExecutor(new TreeCommand(this));
        this.getCommand("xrpginfo").setExecutor(new XRPGInfoCommand(this));
        this.getCommand("xrpgadmin").setExecutor(new XRPGAdminCommand(this, skillLoader));
        this.getCommand("class").setExecutor(new ChangeClassCommand(playerManager, GUIBaseItems));
        System.out.println("RPG classes loaded!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            databaseManager.loadPlayerData(player.getUniqueId());
        }

        int timer = this.getConfig().getInt("garbage-collection.timer", 120);
        if (timer > 0)
            new ClearHashMapTask(this, projectiles).runTaskTimerAsynchronously(this, timer * 20L, timer * 20L);

        if (useMana()) {
            long delay = (long) (this.getConfig().getDouble("mana.recovery-delay", 5.0) * 20);
            new ManaTask(playerManager.getXRPGPlayers(), this.getConfig().getInt("mana.recovery-amount")).runTaskTimerAsynchronously(this, delay, delay);
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        new RemoveBlocklistTask(temporaryBlocks.keySet(), this).run();
        for (UUID uuid : playerManager.getXRPGPlayers().keySet()) {
            XRPGPlayer xrpgPlayer = playerManager.getXRPGPlayer(uuid);
            Utils.removeAllModifiers(xrpgPlayer.getPlayer());
            this.databaseManager.savePlayerData(xrpgPlayer);
        }

        this.databaseManager.disconnect();
    }

/*    private void initTreeMenuGUI(){
        treeMenu = Bukkit.createInventory(null, 9, "Skill Trees");

        for (String treeId:treeData.keySet()) {
            SkillTree tree = treeData.get(treeId);

            ItemStack icon = tree.getIcon();
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.getPersistentDataContainer().set(this.getKey("classId"), PersistentDataType.STRING, treeId);
            icon.setItemMeta(iconMeta);

            treeMenu.addItem(icon);
        }
    }*/

    public static XRPG getInstance(){
        return instance;
    }

    private void initEventListeners(ConfigurationSection dependencyConfig, ConfigurationSection featureSection) {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, databaseManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this, skillLoader, databaseManager), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);
        getServer().getPluginManager().registerEvents(new FollowerListener(this), this);
        if (featureSection.getBoolean("armorsets")){
            getServer().getPluginManager().registerEvents(new ArmorSetListener(this), this);
        }

        Plugin LM = Bukkit.getPluginManager().getPlugin("LevelledMobs");
        if (LM != null && LM.isEnabled() && dependencyConfig.getBoolean("levelled-mobs.enable-hook", false)){
            final ConfigurationSection LMSection = this.getConfig().getConfigurationSection("general-dependencies.levelled-mobs");
            if (LMSection != null){
                int lowerBound = LMSection.getInt("max-negative-level-offset", 0);

                if (lowerBound < 0) //force positive
                    lowerBound = lowerBound * -1;

                final int upperBound = LMSection.getInt("max-positive-level-offset", 0);

                LevelledMobsManager levelledMobsManager = new LevelledMobsManager(this, LM, lowerBound, upperBound);
                getServer().getPluginManager().registerEvents(new LevelledMobsListener(this, levelledMobsManager), this);
            }else{
                Bukkit.getLogger().warning("Could not find section \"levelled-mobs\" under \"general-dependencies\"!");
            }
        }
    }

    private void loadConfigs() {
        this.saveDefaultConfig();
    }

    public ProtectionSet getProtectionSet() {
        return protectionSet;
    }

    public PartySet getPartySet() {
        return partySet;
    }

    public IPvPToggle getPvpToggle() {
        return pvpToggle;
    }

    public ClassInfo getClassInfo(String classId){
        return classInfo.get(classId);
    }

    public HashMap<String, ClassInfo> getClassInfo(){
        return classInfo;
    }

    public ItemStack getSpellbookItem(){
        final String name = getConfig().getString("items.spellbook.name", "Spellbook");
        final List<String> lore = getConfig().getStringList("items.spellbook.lore");

        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(getKey("spellbook"), PersistentDataType.BYTE, (byte)1);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    public List<ItemStack> generateBaseGUIItems(){
        List<ItemStack> items = new ArrayList<>();
        for (String classId:this.getClassInfo().keySet()) {
            ClassInfo info = this.getClassInfo(classId);

            if (info.isEnabled()){
                ItemStack itemStack = Utils.buildItemStack(info.getIcon(), info.getDisplayName(), info.getDescription());
                final ItemMeta meta = itemStack.getItemMeta();
                meta.getPersistentDataContainer().set(getKey("classId"), PersistentDataType.STRING, classId);

                itemStack.setItemMeta(meta);

                items.add(itemStack);
            }
        }
        return items;
    }

    public ClassChangeManager getClassChangeManager() {
        return classChangeManager;
    }

    public HashMap<String, SkillTree> getTreeData(){ return treeData; }

    public HashMap<Location, Material> getTemporaryBlocks() {
        return temporaryBlocks;
    }

    public NamespacedKey getKey(String keyName){
        return keyRegistry.get(keyName);
    }

    public boolean useMana() {
        return this.getConfig().getBoolean("mana.enabled", false);
    }

    @Nullable
    public SkillData getSkillData(String skillId){
        return skillData.get(skillId);
    }

    public HashMap<String, SkillData> getSkillData(){
        return this.skillData;
    }

    public boolean hasSkillData(String skillId){
        return skillData.containsKey(skillId);
    }

    public Set<String> getAllLoadedSkillIds(){
        return skillData.keySet();
    }

    public SkillTree getSkillTree(String treeId){
        return treeData.get(treeId);
    }

    public boolean isTreeViewer(Player player){
        return treeView.containsKey(player.getUniqueId());
    }

    public boolean isTreeViewer(UUID playerUUID){
        return treeView.containsKey(playerUUID);
    }

    public TreeData getTreeView(UUID playerUUID){
        return treeView.get(playerUUID);
    }

    public void addTreeViewer(UUID playerUUID, TreeData treeData){
        this.treeView.put(playerUUID, treeData);
    }

    public TreeData removeTreeViewer(UUID playerUUID){
        return treeView.remove(playerUUID);
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    public DependencyManager getDependencyManager(){
        return dependencyManager;
    }

    public ArmorManager getArmorManager() {
        return armorManager;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getSpellDamageMultiplier() {
        return spellDamageMultiplier;
    }
}