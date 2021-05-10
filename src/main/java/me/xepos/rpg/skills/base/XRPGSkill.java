package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.dependencies.parties.IPartyManager;
import me.xepos.rpg.dependencies.protection.ProtectionSet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public abstract class XRPGSkill {

    private XRPGPlayer xrpgPlayer;
    private final XRPG plugin;
    private final ProtectionSet protectionSet;
    private final IPartyManager partyManager;

    //Stats
    private final ConfigurationSection skillVariables;
    private long remainingCooldown;

    public XRPGSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        this.xrpgPlayer = xrpgPlayer;
        this.plugin = plugin;
        this.skillVariables = skillVariables;
        this.protectionSet = plugin.getProtectionSet();
        this.partyManager = plugin.getPartyManager();
        this.remainingCooldown = System.currentTimeMillis();

        if (this instanceof IFollowerContainer) {
            xrpgPlayer.getFollowerSkills().add((IFollowerContainer) this);
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

    public String getName() {
        return skillVariables.getString("name", this.getClass().getSimpleName());
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

    protected boolean hasCastItem() {
        String tag = skillVariables.getString("required-tag");
        ItemStack item = xrpgPlayer.getPlayer().getInventory().getItemInMainHand();
        if (tag != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return false;

            String itemTag = meta.getPersistentDataContainer().get(plugin.getTagKey(), PersistentDataType.STRING);

            return itemTag != null && itemTag.equalsIgnoreCase(tag);

        } else {
            List<String> allowedItems = skillVariables.getStringList("required-items");
            for (String itemName : allowedItems) {
                //So axe doesn't trigger pickaxe etc
                if (itemName.equalsIgnoreCase("axe")) {
                    itemName = "_" + itemName;
                }
                if (item.getType().name().endsWith(itemName.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
    }
}
