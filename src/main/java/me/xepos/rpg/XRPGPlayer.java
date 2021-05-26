package me.xepos.rpg;

import me.xepos.rpg.datatypes.ClassData;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.handlers.ActiveEventHandler;
import me.xepos.rpg.handlers.BowEventHandler;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.IFollowerContainer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class XRPGPlayer {
    private transient UUID playerId;
    private transient Player player;
    private transient String classDisplay;
    private int currentMana;
    private int maximumMana;
    private long lastClassChangeTime;
    private String classId;
    private int freeChangeTickets = 2;
    private boolean spellCastModeEnabled = false;
    private List<String> spellKeybinds = new ArrayList<>();
    private int level;
    private double currentExp;

    //Status Effects
    public transient ConcurrentHashMap<String, Double> dmgTakenMultipliers = new ConcurrentHashMap<>();
    private transient double damageTakenMultiplier = 1.0;
    private transient boolean canUseShield = true;
    private transient boolean isStunned = false;
    private transient long lastStunTime = 0;

    public XRPGPlayer(UUID playerId, PlayerData playerData) {
        this.player = null;
        this.playerId = playerId;
        this.classId = playerData.getClassId();
        this.lastClassChangeTime = playerData.getLastClassChange();
        this.freeChangeTickets = playerData.getFreeChangeTickets();
        this.spellKeybinds.clear();
        //Need to check for null as this will be new for new players
        if (playerData.getClassData(classId) != null)
            this.spellKeybinds.addAll(playerData.getClassData(playerData.getClassId()).getKeyBindOrder());

        if (handlerList.isEmpty())
            initializePassiveHandlers();

        if (activeHandler == null)
            activeHandler = new ActiveEventHandler(this);
    }

    //Constructor for loading profiles
    @Deprecated
    public XRPGPlayer(UUID playerId, String classId) {
        this.player = null;
        this.playerId = playerId;
        this.classId = classId;
        this.lastClassChangeTime = 0;

        if (handlerList.isEmpty())
            initializePassiveHandlers();

        if (activeHandler == null)
            activeHandler = new ActiveEventHandler(this);
    }

    @Deprecated
    public XRPGPlayer(Player player, String classId) {
        this.player = player;
        this.playerId = player.getUniqueId();
        this.classId = classId;
        this.lastClassChangeTime = 0;

        if (handlerList.isEmpty())
            initializePassiveHandlers();

        if (activeHandler == null)
            activeHandler = new ActiveEventHandler(this);
    }

    private void initializePassiveHandlers() {
        handlerList.put("RIGHT_CLICK", new PassiveEventHandler());
        handlerList.put("LEFT_CLICK", new PassiveEventHandler());
        handlerList.put("SNEAK_RIGHT_CLICK", new PassiveEventHandler());
        handlerList.put("SNEAK_LEFT_CLICK", new PassiveEventHandler());

        //Damage Handlers
        handlerList.put("DAMAGE_DEALT", new PassiveEventHandler());
        handlerList.put("DAMAGE_TAKEN", new PassiveEventHandler());
        handlerList.put("DAMAGE_TAKEN_ENVIRONMENTAL", new PassiveEventHandler());

        //Bow Handlers
        handlerList.put("SHOOT_BOW", new BowEventHandler(this));

        //Movement Handlers
        handlerList.put("SPRINT", new PassiveEventHandler());
        handlerList.put("JUMP", new PassiveEventHandler());
        handlerList.put("SNEAK", new PassiveEventHandler());

        //Other Handlers
        handlerList.put("SWAP_HELD_ITEM", new PassiveEventHandler());
        handlerList.put("HEALTH_REGEN", new PassiveEventHandler());
        handlerList.put("CONSUME_ITEM", new PassiveEventHandler());
    }

    //For convenience
    private transient List<IFollowerContainer> followerSkills = new ArrayList<>();

    private transient ActiveEventHandler activeHandler;
    private final transient HashMap<String, PassiveEventHandler> handlerList = new HashMap<>();


    public int getFreeChangeTickets() {
        return freeChangeTickets;
    }

    public void setFreeChangeTickets(int freeChangeTickets) {
        this.freeChangeTickets = freeChangeTickets;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned) {
        isStunned = stunned;
        if (stunned)
            lastStunTime = System.currentTimeMillis();
    }

    public int getStunblockDuration() {
        return (int) ((lastStunTime - System.currentTimeMillis()) / 1000);
    }

    public boolean canBeStunned() {
        return System.currentTimeMillis() > lastStunTime + 20 * 1000L;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public List<IFollowerContainer> getFollowerSkills() {
        return followerSkills;
    }

    public void setFollowerSkills(List<XRPGSkill> skills) {
        followerSkills.clear();
        for (XRPGSkill skill : skills) {
            if (skill instanceof IFollowerContainer) {
                followerSkills.add((IFollowerContainer) skill);
            }
        }
    }

    public String getClassDisplayName() {
        return classDisplay;
    }

    public void resetClassData(String classId, String classDisplayName) {
        if (classId == null || classId.equals("")) return;

        this.classId = classId;
        this.classDisplay = classDisplayName;

        //Clearing keybinds
        spellKeybinds.clear();

        //Clearing skills
        activeHandler.getSkills().clear();
        for (PassiveEventHandler handler : handlerList.values()) {
            handler.clear();
        }
    }

    public boolean isShieldAllowed() {
        return canUseShield;
    }

    public void setShieldAllowed(boolean canUseShield) {
        this.canUseShield = canUseShield;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = currentMana;
    }

    public void addMana(int value) {
        if (this.currentMana >= maximumMana) return;

        if (this.currentMana + value > maximumMana) {
            currentMana = maximumMana;
        } else {
            this.currentMana += value;
        }
    }

    public void addMana(int value, boolean force) {
        if (force)
            this.currentMana += value;
        else
            this.addMana(value);
    }

    public void removeMana(int value) {
        this.currentMana -= value;
    }

    public int getMaximumMana() {
        return maximumMana;
    }

    public void setMaximumMana(int maximumMana) {
        this.maximumMana = maximumMana;
    }

    public double getDamageTakenMultiplier() {
        return damageTakenMultiplier;
    }

    public void recalculateDamageTakenMultiplier() {
        double base = 1.0;
        for (String id : dmgTakenMultipliers.keySet()) {
            base *= dmgTakenMultipliers.get(id);
        }
        this.damageTakenMultiplier = base;
    }

    public long getLastClassChangeTime() {
        return lastClassChangeTime;
    }

    public void setLastClassChangeTime(long lastClassChangeTime) {
        this.lastClassChangeTime = lastClassChangeTime;
    }

    public boolean isSpellCastModeEnabled() {
        return spellCastModeEnabled;
    }

    public void setSpellCastModeEnabled(boolean spellCastModeEnabled) {
        this.spellCastModeEnabled = spellCastModeEnabled;
    }

    public String getSkillForSlot(int slotId){
        return spellKeybinds.get(slotId);
    }

    //////////////////////////////////
    //                              //
    //            Levels            //
    //                              //
    //////////////////////////////////

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level == this.level || level < 1) return;

        if (level < this.level) {
            this.currentExp = 0;
        }

        this.level = level;
    }

    public double getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(double currentExp) {
        this.currentExp = currentExp;

        tryLevelUp();
    }

    public void addLevels(int amount) {
        if (amount < 1) return;
        this.level += amount;
    }

    public void addExp(double amount) {
        this.currentExp += amount;

        tryLevelUp();
    }


    private void tryLevelUp() {
        final double requiredExp = getRequiredExp();

        if (this.currentExp >= requiredExp){
            this.level++;
            this.currentExp -= requiredExp;

            tryLevelUp();
        }
    }

    private double getRequiredExp() {
        return (4 * (Math.pow(level, 3))) / 5 + 100;
    }

    //////////////////////////////////
    //                              //
    //  Handlers getters & setters  //
    //                              //
    //////////////////////////////////

    public PassiveEventHandler getPassiveEventHandler(String handlerName) {
        return handlerList.get(handlerName.toUpperCase());
    }

    public HashMap<String, PassiveEventHandler> getPassiveHandlerList() {
        return handlerList;
    }

    public void addPassiveEventHandler(String handlerName, PassiveEventHandler handler) {
        this.handlerList.put(handlerName.toUpperCase(), handler);
    }

    public ActiveEventHandler getActiveHandler() {
        return activeHandler;
    }

    //////////////////////////////////
    //                              //
    //             Data             //
    //                              //
    //////////////////////////////////

    public Set<String> getAllLearnedSkills(){
        Set<String> skills = new HashSet<>();
        for (PassiveEventHandler handler : handlerList.values()) {
            skills.addAll(handler.getSkills().keySet());
        }
        skills.addAll(activeHandler.getSkills().keySet());

        return skills;
    }

    public PlayerData extractData() {
        Set<String> skills = getAllLearnedSkills();

        Set<String> keybindOrder = new HashSet<>(spellKeybinds);

        PlayerData playerData = new PlayerData(this.classId, this.freeChangeTickets, this.lastClassChangeTime);
        playerData.addClassData(this.classId, new ClassData(this.getPlayer().getHealth(), skills, keybindOrder));

        return playerData;
    }

    public List<String> getSpellKeybinds() {
        return spellKeybinds;
    }

    public void setSpellKeybinds(List<String> spellKeybinds) {
        this.spellKeybinds = spellKeybinds;
    }
}
