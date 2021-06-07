package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.dependencies.combat.parties.IPartyManager;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class XRPGSkill {

    private int skillLevel;
    private final XRPGPlayer xrpgPlayer;
    private final XRPG plugin;
    private final ProtectionSet protectionSet;
    private final IPartyManager partyManager;

    //Stats
    private final ConfigurationSection skillVariables;
    private long remainingCooldown;

    public XRPGSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        this.xrpgPlayer = xrpgPlayer;
        this.plugin = plugin;
        this.skillVariables = skillVariables;
        this.protectionSet = plugin.getProtectionSet();
        this.partyManager = plugin.getPartyManager();
        this.remainingCooldown = System.currentTimeMillis();
        this.skillLevel = skillLevel;

        if (this instanceof IMessenger) {
            xrpgPlayer.getMessengerSkills().add((IMessenger) this);
        }
    }


    public abstract void activate(Event event);

    public abstract void initialize();

    public long getRemainingCooldown() {
        return remainingCooldown;
    }

    public void setRemainingCooldown(double cooldownInSeconds) {
        this.remainingCooldown = System.currentTimeMillis() + ((long) cooldownInSeconds * 1000);
    }

    public String getSkillName() {
        return skillVariables.getString("name", "");
    }

    public boolean isSkillReady() {
        return remainingCooldown <= System.currentTimeMillis();
    }

    protected XRPG getPlugin() {
        return this.plugin;
    }

    protected ProtectionSet getProtectionSet() {
        return protectionSet;
    }

    protected IPartyManager getPartyManager() {
        return partyManager;
    }

    @SuppressWarnings("all")
    protected List<Player> getNearbyAlliedPlayers(Player caster, double x, double y, double z) {
        return (List<Player>) new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), x, y, z, p -> p instanceof Player && p != caster && partyManager.isPlayerAllied(caster, (Player) p)));
    }

    protected boolean canApplyBuffToFriendly(Player target){
        return partyManager.isPlayerAllied(xrpgPlayer.getPlayer(), target) && protectionSet.isPvPTypeSame(xrpgPlayer.getPlayer().getLocation(), target.getLocation());
    }

    protected boolean isTargetAllied(Player target){
        return !partyManager.isPlayerAllied(xrpgPlayer.getPlayer(), target);
    }

    public XRPGPlayer getXRPGPlayer() {
        return xrpgPlayer;
    }

    public double getCooldown() {
        return skillVariables.getDouble("cooldown", -1);
    }

    public double getDamage() {
        return skillVariables.getDouble("damage", 0);
    }

    public double getDamageMultiplier() {
        return skillVariables.getDouble("damage-multiplier");
    }

    public int getRequiredMana() {
        if (plugin.useMana()) {
            return skillVariables.getInt("mana", 0);
        }

        return 0;
    }

    public ConfigurationSection getSkillVariables() {
        return skillVariables;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }
}
