package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class XRPGSkill {

    private int skillLevel;
    private final boolean isEventSkill;
    private final XRPGPlayer xrpgPlayer;
    private final XRPG plugin;
    private final ProtectionSet protectionSet;
    private final PartySet partySet;

    //Stats
    private final SkillData skillVariables;
    private long remainingCooldown;

    public XRPGSkill(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        this.xrpgPlayer = xrpgPlayer;
        this.plugin = plugin;
        this.skillVariables = skillVariables;
        this.protectionSet = plugin.getProtectionSet();
        this.partySet = plugin.getPartySet();
        this.remainingCooldown = System.currentTimeMillis();
        this.skillLevel = skillLevel;
        this.isEventSkill = isEventSkill;

        if (this instanceof IMessenger) {
            xrpgPlayer.getMessengerSkills().add((IMessenger) this);
        }else if (this instanceof IAttributable && xrpgPlayer.getPlayer() == null){
            xrpgPlayer.addModifiersToApplyOnJoin(((IAttributable)this).getModifiersToApply());
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
        return skillVariables.getName();
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

    protected PartySet getPartySet() {
        return partySet;
    }

    @SuppressWarnings("all")
    protected List<Player> getNearbyAlliedPlayers(Player caster, double x, double y, double z) {
        return (List<Player>) new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), x, y, z, p -> p instanceof Player && partySet.isPlayerAllied(caster, (Player) p)));
    }

    protected boolean canApplyBuffToFriendly(Player target){
        return partySet.isPlayerAllied(xrpgPlayer.getPlayer(), target) && protectionSet.isPvPTypeSame(xrpgPlayer.getPlayer().getLocation(), target.getLocation());
    }

    protected boolean canHurtTarget(Player target){
        return partySet.isPlayerAllied(xrpgPlayer.getPlayer(), target) && partySet.canHurtPlayer(xrpgPlayer.getPlayer(), target);
    }

    public XRPGPlayer getXRPGPlayer() {
        return xrpgPlayer;
    }

    public double getCooldown() {
        return skillVariables.getCooldown(getSkillLevel());
    }

    public double getRawDamage() {
        return skillVariables.getDamage(getSkillLevel());
    }

    public double getDamage(){
        return skillVariables.getDamage(getSkillLevel()) * (1 + xrpgPlayer.getLevel() * plugin.getSpellDamageMultiplier());
    }

    public double getDamage(LivingEntity entity){
        double toughness = 0;

        AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        if (instance != null){
            if (instance.getValue() > 0) {
                toughness = instance.getValue() + 8;
            }
        }
        if (entity instanceof Player) return toughness + getRawDamage();

        return toughness + getDamage();
    }

    public double getDamageMultiplier() {
        return (skillVariables.getDouble(getSkillLevel(), "damage-multiplier", 0) + 100) / 100;
    }

    public int getRequiredMana() {
        if (plugin.useMana()) {
            return skillVariables.getMana(getSkillLevel());
        }

        return 0;
    }

    public boolean hasRequiredMana(){
        return getRequiredMana() <= xrpgPlayer.getCurrentMana();
    }

    public void updatedCasterMana(){
        xrpgPlayer.setCurrentMana(xrpgPlayer.getCurrentMana() - skillVariables.getMana(getSkillLevel()));
        xrpgPlayer.sendActionBarMessage();
    }

    public void sendNotEnoughManaMessage(){
        xrpgPlayer.getPlayer().sendMessage(ChatColor.RED + "You do not have enough mana to cast " + getSkillName() + "!");
    }

    public SkillData getSkillVariables() {
        return skillVariables;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public boolean isEventSkill() {
        return isEventSkill;
    }
}
