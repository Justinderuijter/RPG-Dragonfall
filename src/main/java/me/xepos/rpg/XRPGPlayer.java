package me.xepos.rpg;

import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.datatypes.ClassData;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.handlers.EventHandler;
import me.xepos.rpg.handlers.ShootBowEventHandler;
import me.xepos.rpg.skills.base.IFollowerContainer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
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

    //Status Effects
    public transient ConcurrentHashMap<String, Double> dmgTakenMultipliers = new ConcurrentHashMap<>();
    private transient double damageTakenMultiplier = 1.0;
    private transient boolean canUseShield = true;
    private transient boolean isStunned = false;
    private transient long lastStunTime = 0;

    public XRPGPlayer(UUID playerId, PlayerData playerData){
        this.player = null;
        this.playerId = playerId;
        this.classId = playerData.getClassId();
        this.lastClassChangeTime = playerData.getLastClassChange();
        this.freeChangeTickets = playerData.getFreeChangeTickets();
    }

    //Constructor for loading profiles
    public XRPGPlayer(UUID playerId, String classId) {
        this.player = null;
        this.playerId = playerId;
        this.classId = classId;
        this.lastClassChangeTime = 0;
    }


    public XRPGPlayer(Player player, String classId) {
        this.player = player;
        this.playerId = player.getUniqueId();
        this.classId = classId;
        this.lastClassChangeTime = 0;
    }

    //For convenience
    private transient List<IFollowerContainer> followerSkills = new ArrayList<>();

    private final transient HashMap<String, EventHandler> handlerList = new HashMap<String, EventHandler>() {{
        //Interact Handlers
        put("RIGHT_CLICK", new EventHandler());
        put("LEFT_CLICK", new EventHandler());
        put("SNEAK_RIGHT_CLICK", new EventHandler());
        put("SNEAK_LEFT_CLICK", new EventHandler());

        //Interact Handlers (Entity)
        put("RIGHT_CLICK_ENTITY", new EventHandler());
        put("SNEAK_RIGHT_CLICK_ENTITY", new EventHandler());

        //Damage Handlers
        put("DAMAGE_DEALT", new EventHandler());
        put("DAMAGE_TAKEN", new EventHandler());
        put("DAMAGE_TAKEN_ENVIRONMENTAL", new EventHandler());

        //Bow Handlers
        put("SHOOT_BOW", new ShootBowEventHandler());

        //Movement Handlers
        put("SPRINT", new EventHandler());
        put("JUMP", new EventHandler());

        //Other Handlers
        put("HEALTH_REGEN", new EventHandler());
        put("CONSUME_ITEM", new EventHandler());

    }};


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

        for (EventHandler handler : handlerList.values()) {
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

    public double getDamageTakenMultiplier(){
        return damageTakenMultiplier;
    }

    public void recalculateDamageTakenMultiplier(){
        double base = 1.0;
        for (String id:dmgTakenMultipliers.keySet()) {
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

    //////////////////////////////////
    //                              //
    //  Handlers getters & setters  //
    //                              //
    //////////////////////////////////

    public EventHandler getEventHandler(String handlerName) {
        return handlerList.get(handlerName.toUpperCase());
    }

    public HashMap<String, EventHandler> getHandlerList() {
        return handlerList;
    }

    public void addEventHandler(String handlerName, EventHandler handler) {
        this.handlerList.put(handlerName.toUpperCase(), handler);
    }

    //////////////////////////////////
    //                              //
    //             Data             //
    //                              //
    //////////////////////////////////

    public PlayerData extractData(){
        Set<String> skills = new HashSet<>();
        for (EventHandler handler:handlerList.values()) {
            skills.addAll(handler.getSkills().keySet());
        }

        PlayerData playerData = new PlayerData(this.classId, this.freeChangeTickets, this.lastClassChangeTime);
        playerData.addClassData(this.classId, new ClassData(this.getPlayer().getHealth(), skills));

        return playerData;
    }
}
