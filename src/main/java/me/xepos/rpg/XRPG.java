package me.xepos.rpg;

import me.xepos.rpg.commands.*;
import me.xepos.rpg.configuration.ClassLoader;
import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.configuration.TreeLoader;
import me.xepos.rpg.database.DatabaseManagerFactory;
import me.xepos.rpg.database.IDatabaseManager;
import me.xepos.rpg.datatypes.BaseProjectileData;
import me.xepos.rpg.datatypes.ClassInfo;
import me.xepos.rpg.datatypes.TreeData;
import me.xepos.rpg.dependencies.combat.parties.PartyManagerFactory;
import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSetFactory;
import me.xepos.rpg.dependencies.combat.pvptoggle.IPvPToggle;
import me.xepos.rpg.dependencies.combat.pvptoggle.PvPToggleFactory;
import me.xepos.rpg.listeners.*;
import me.xepos.rpg.tasks.ClearHashMapTask;
import me.xepos.rpg.tasks.ManaTask;
import me.xepos.rpg.tree.SkillTree;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public final class XRPG extends JavaPlugin {

    private static XRPG instance;

    private List<ItemStack> GUIBaseItems;
    private SkillLoader skillLoader;
    private TreeLoader treeLoader;

    //Ability targetting managers
    private PartySet partySet;
    private ProtectionSet protectionSet;
    private IPvPToggle pvpToggle;

    //Data manager
    private IDatabaseManager databaseManager;

    //Classes
    private static HashMap<String, ClassInfo> classInfo;

    //Skills
    private static HashMap<String, FileConfiguration> skillData;

    //Skill trees
    private static HashMap<String, SkillTree> treeData;

    //Players
    private static final ConcurrentHashMap<UUID, XRPGPlayer> RPGPlayers = new ConcurrentHashMap<>();

    //Tree viewer
    private static HashMap<UUID, TreeData> treeView;
    //Custom projectiles
    public final ConcurrentHashMap<UUID, BaseProjectileData> projectiles = new ConcurrentHashMap<>();

    //Keys
    private static final HashMap<String, NamespacedKey> keyRegistry = new HashMap<>();

    @Override // Plugin startup logic
    public void onEnable() {
        instance = this;

        Plugin mcMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
        Plugin pvpToggle = Bukkit.getPluginManager().getPlugin("mcMMO");

        boolean useMcMMO = false;
        boolean usePvPToggle = false;

        if (mcMMO != null) useMcMMO = true;
        if (pvpToggle != null) usePvPToggle = true;



        //Load classes
        this.saveDefaultConfig();

        //Loaders
        this.skillLoader = new SkillLoader(this);
        this.skillData = this.skillLoader.initializeSkills();
        this.treeLoader = new TreeLoader(this);
        this.treeData = treeLoader.initialize();
        this.classInfo = new ClassLoader(this).initializeClasses();

        this.treeView = new HashMap<>();


        final String[] keyNames = new String[]{"tag", "separator", "classId", "skillId", "spellbook", "requires", "level", "maxLevel", "attribute"};

        for (String name:keyNames) {
            this.keyRegistry.put(name, new NamespacedKey(this, name));
        }


        //Load database
        this.databaseManager = DatabaseManagerFactory.getDatabaseManager(skillLoader);

        //Load ability targetting managers
        this.partySet = new PartySet(PvPToggleFactory.getPvPToggle(usePvPToggle), PartyManagerFactory.getPartyManager());
        this.protectionSet = ProtectionSetFactory.getProtectionRules();

        //Prevents throwing error if databaseManager shuts down this plugin.
        if (!this.isEnabled())
            return;

        //CraftLoader disabled as it won't be used (for now)
        //new CraftLoader(this).initCustomRecipes();

        this.GUIBaseItems = generateBaseGUIItems();
        //registering listeners/commands
        initEventListeners();

        if (useMcMMO){
            Bukkit.getLogger().info("Using mcMMO for EXP calculations.");
            getServer().getPluginManager().registerEvents(new McMMOListener(this), this);
        }else{
            getServer().getPluginManager().registerEvents(new EXPListener(this), this);
        }

        this.pvpToggle = PvPToggleFactory.getPvPToggle(usePvPToggle);


        this.getCommand("xrpgdebug").setExecutor(new XRPGDebug(this));
        this.getCommand("xrpgreload").setExecutor(new XRPGReload());
        this.getCommand("spellmode").setExecutor(new ToggleSpellCommand(this));
        this.getCommand("spellbook").setExecutor(new SpellbookCommand(this));
        this.getCommand("tree").setExecutor(new TreeCommand(this, this.databaseManager));
        this.getCommand("xrpginfo").setExecutor(new XRPGInfoCommand(this));
        this.getCommand("xrpgadmin").setExecutor(new XRPGAdminCommand(this, skillLoader));
        this.getCommand("class").setExecutor(new ChangeClassCommand(this, GUIBaseItems));
        System.out.println("RPG classes loaded!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            databaseManager.loadPlayerData(player.getUniqueId());
        }

        int timer = this.getConfig().getInt("garbage-collection.timer", 120);
        if (timer > 0)
            new ClearHashMapTask(this, projectiles).runTaskTimerAsynchronously(this, timer * 20L, timer * 20L);

        if (useMana()) {
            long delay = (long) (this.getConfig().getDouble("mana.recovery-delay", 5.0) * 20);
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

    private void initEventListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, databaseManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this, skillLoader, databaseManager), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);
        getServer().getPluginManager().registerEvents(new FollowerListener(this), this);
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

    public XRPGPlayer getXRPGPlayer(Player player, boolean force) {
        XRPGPlayer xrpgPlayer = RPGPlayers.get(player.getUniqueId());
        if (force || xrpgPlayer.isClassEnabled()){
            return xrpgPlayer;
        }

        return null;
    }

    public XRPGPlayer getXRPGPlayer(Player player){
        return this.getXRPGPlayer(player, false);
    }

    public XRPGPlayer getXRPGPlayer(UUID playerUUID, boolean force) {
        XRPGPlayer xrpgPlayer = RPGPlayers.get(playerUUID);
        if (force || xrpgPlayer.isClassEnabled()){
            return xrpgPlayer;
        }

        return null;
    }

    public XRPGPlayer getXRPGPlayer(UUID playerUUID) {
        return this.getXRPGPlayer(playerUUID, false);
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

    public HashMap<String, SkillTree> getTreeData(){ return treeData; }

    public NamespacedKey getKey(String keyName){
        return keyRegistry.get(keyName);
    }

    public boolean useMana() {
        return this.getConfig().getBoolean("mana.enabled", false);
    }

    public FileConfiguration getSkillData(String skillId){
        return skillData.get(skillId);
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
}