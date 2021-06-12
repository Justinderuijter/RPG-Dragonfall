package me.xepos.rpg;

import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.datatypes.ClassData;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.handlers.ActiveEventHandler;
import me.xepos.rpg.handlers.BowEventHandler;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.IMessenger;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class XRPGPlayer {
    private static final int MAX_LEVEL = XRPG.getInstance().getConfig().getInt("leveling.max-level", 30);
    private static final int UPGRADE_INTERVAL = XRPG.getInstance().getConfig().getInt("leveling.upgrade-point-interval", 5);
    private static final int UNLOCK_INTERVAL = XRPG.getInstance().getConfig().getInt("leveling.unlock-point-interval", 1);

    private transient UUID playerId;
    private transient Player player;
    private transient String classDisplay;
    private boolean isClassEnabled;
    private int currentMana;
    private int baseMana;
    private int levelMana;
    private int healthLevel;
    private long lastBookReceivedTime;
    private long lastClassChangeTime;
    private String classId;
    private byte skillUnlockPoints = 0;
    private byte skillUpgradePoints = 0;
    private boolean spellCastModeEnabled = false;
    private List<String> spellKeybinds = new ArrayList<>();
    private int level;
    private double currentExp;

    //Status Effects
    public transient ConcurrentHashMap<String, Double> dmgTakenMultipliers = new ConcurrentHashMap<>();
    private transient double damageTakenMultiplier = 1.0;
    private transient boolean isStunned = false;
    private transient long lastStunTime = 0;

    //This will ever only be called when joining
    private List<AttributeModifierData> modifiersToApply = new ArrayList<>();
    private final Set<PotionEffectType> permanentEffects = new HashSet<>();

    public XRPGPlayer(UUID playerId, PlayerData playerData) {
        XRPG plugin = XRPG.getInstance();

        this.player = null;
        this.playerId = playerId;
        this.isClassEnabled = playerData.isClassEnabled();
        this.classId = playerData.getClassId();
        this.lastClassChangeTime = playerData.getLastClassChange();
        this.lastBookReceivedTime = playerData.getLastBookReceived();
        this.spellKeybinds.clear();

        //New players won't have a classId
        if (!StringUtils.isBlank(this.classId)) {
            final ClassData data = playerData.getClassData(this.classId);

            this.classDisplay = plugin.getClassInfo(classId).getDisplayName();
            this.level = data.getLevel();
            this.currentExp = data.getExperience();
            this.currentMana = data.getLastMana();
            this.baseMana = plugin.getClassInfo(classId).getBaseMana();
            this.healthLevel = data.getHealthLevel();
            this.skillUnlockPoints = data.getSkillUnlockPoints();
            this.skillUpgradePoints = data.getSkillUpgradePoints();

            this.spellKeybinds.addAll(data.getKeybindOrder());

        }

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
    private transient List<IMessenger> messengerSkills = new ArrayList<>();

    private transient ActiveEventHandler activeHandler;
    private final transient HashMap<String, PassiveEventHandler> handlerList = new HashMap<>();

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

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public List<IMessenger> getMessengerSkills() {
        return messengerSkills;
    }

    public String getClassDisplayName() {
        return classDisplay;
    }

    public void resetPlayerDataForClassChange(PlayerData playerData, String classDisplayName) {
        if (StringUtils.isBlank(playerData.getClassId())) return;

        this.classId = playerData.getClassId();
        this.classDisplay = classDisplayName;
        this.isClassEnabled = playerData.isClassEnabled();
        this.lastClassChangeTime = playerData.getLastClassChange();
        this.lastBookReceivedTime = playerData.getLastBookReceived();
        ClassData classData = playerData.getClassData(playerData.getClassId());
        if (classData != null) {
            this.level = classData.getLevel();
            this.currentExp = classData.getExperience();
            this.currentMana = classData.getLastMana();
            this.levelMana = classData.getManaLevel();
            this.healthLevel = classData.getHealthLevel();
            this.baseMana = classData.getBaseMana();
            if (classData.getLastMana() == -1) {
                this.currentMana = baseMana;
            }
            this.skillUpgradePoints = classData.getSkillUpgradePoints();
            this.skillUnlockPoints = classData.getSkillUnlockPoints();
        }

        //Clearing keybinds
        spellKeybinds.clear();

        //Clearing skills
        activeHandler.getSkills().clear();
        for (PassiveEventHandler handler : handlerList.values()) {
            handler.clear();
        }

        Utils.removeAllModifiers(player);
        clearAllPermanentPotionEffects();
        AttributeModifierManager.getInstance().reapplyHealthAttribute(player, healthLevel);
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = currentMana;
    }

    public void addMana(int value) {
        if (this.currentMana >= baseMana + levelMana) return;

        if (this.currentMana + value > baseMana + levelMana) {
            currentMana = baseMana + levelMana;
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
        return baseMana + levelMana;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public void setBaseMana(int baseMana) {
        this.baseMana = baseMana;
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

    public void addModifiersToApplyOnJoin(List<AttributeModifierData> modifierData) {
        modifiersToApply.addAll(modifierData);
    }

    public void addQueuedModifiers() {
        for (AttributeModifierData mod : modifiersToApply) {
            Utils.addUniqueModifier(player, mod);
        }
        AttributeModifierManager.getInstance().reapplyHealthAttribute(player, healthLevel);
        modifiersToApply = Collections.emptyList();
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

    public String getSkillForSlot(int slotId) {
        return spellKeybinds.get(slotId);
    }

    public boolean isClassEnabled() {
        return isClassEnabled;
    }

    public void setClassEnabled(boolean classEnabled) {
        isClassEnabled = classEnabled;
        if (!classEnabled) {
            spellCastModeEnabled = false;
        }
    }

    public void sendActionBarMessage() {
        StringBuilder message = new StringBuilder();
        for (IMessenger messenger : this.messengerSkills) {
            message.append(messenger.getMessage()).append(ChatColor.WHITE).append(" | ");
        }
        message.append("Mana: ").append(ChatColor.BLUE).append(currentMana).append(ChatColor.WHITE).append("/").append(ChatColor.BLUE).append(baseMana + levelMana);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message.toString()));
    }

    public int getManaLevel() {
        return levelMana;
    }

    public void setManaLevel(int manaLevel) {
        this.levelMana = manaLevel;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(int healthLevel) {
        this.healthLevel = healthLevel;
    }

    public void addPermanentPotionEffect(PotionEffectType potionEffectType, int amplifier){
        this.permanentEffects.add(potionEffectType);
        player.addPotionEffect(new PotionEffect(potionEffectType, Integer.MAX_VALUE, amplifier, false, false, true));
    }

    public void removePermanentPotionEffect(PotionEffectType potionEffectType){
        this.permanentEffects.remove(potionEffectType);
        player.removePotionEffect(potionEffectType);
    }

    public void clearAllPermanentPotionEffects(){
        for (PotionEffectType potionEffectType:this.permanentEffects) {
            removePermanentPotionEffect(potionEffectType);
        }
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
        final double requiredExp = getRequiredExpToLevel(this.level);

        if (this.level < MAX_LEVEL) {
            if (this.currentExp >= requiredExp) {
                this.level++;
                this.currentExp -= requiredExp;
                this.player.sendMessage(ChatColor.GREEN + "You leveled up!");
                if (level % UNLOCK_INTERVAL == 0) {
                    skillUnlockPoints++;
                    this.player.sendMessage(ChatColor.GREEN + "You gained an unlock point!");
                } else if (level % UPGRADE_INTERVAL == 0) {
                    skillUpgradePoints++;
                    this.player.sendMessage(ChatColor.GREEN + "You gained an upgrade point!");
                }

                tryLevelUp();
            }
        }
    }

    public double getRequiredExpToLevel(int level) {
        return 4 * (4 * (Math.pow(level, 3))) + 1000;
    }

    public int getSkillUnlockPoints() {
        return skillUnlockPoints;
    }

    public void setSkillUnlockPoints(byte skillUnlockPoints) {
        this.skillUnlockPoints = skillUnlockPoints;
    }

    public void addSkillUnlockPoints(byte skillUnlockPoints){
        this.skillUpgradePoints += skillUnlockPoints;
    }

    public int getSkillUpgradePoints() {
        return skillUpgradePoints;
    }

    public void setSkillUpgradePoints(byte skillUpgradePoints) {
        this.skillUpgradePoints = skillUpgradePoints;
    }

    public void addSkillUpgradePoints(byte skillUpgradePoints){
        this.skillUpgradePoints += skillUpgradePoints;
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

    public HashMap<String, XRPGSkill> getAllLearnedSkills() {
        HashMap<String, XRPGSkill> skills = new HashMap<>();
        for (PassiveEventHandler handler : handlerList.values()) {
            for (String skillId : handler.getSkills().keySet()) {
                skills.put(skillId, handler.getSkills().get(skillId));
            }
        }

        for (String skillId : activeHandler.getSkills().keySet()) {

            skills.put(skillId, activeHandler.getSkills().get(skillId));
        }

        return skills;
    }

    public PlayerData extractData() {
        HashMap<String, Integer> skills = new HashMap<>();
        HashMap<String, XRPGSkill> learnedSkills = getAllLearnedSkills();

        for (String skillId : learnedSkills.keySet()) {
            skills.put(skillId, learnedSkills.get(skillId).getSkillLevel());
        }

        PlayerData playerData = new PlayerData(this.classId, this.lastClassChangeTime, this.lastBookReceivedTime, this.isClassEnabled);
        if (StringUtils.isNotBlank(this.classId)) {
            playerData.addClassData(this.classId, new ClassData(this.level, this.currentExp, (byte) this.currentMana, this.levelMana, this.healthLevel, this.skillUpgradePoints, this.skillUnlockPoints, skills, this.spellKeybinds));
        }

        return playerData;
    }

    public List<String> getSpellKeybinds() {
        return spellKeybinds;
    }

    public void setSpellKeybinds(List<String> spellKeybinds) {
        this.spellKeybinds = spellKeybinds;
    }
}
