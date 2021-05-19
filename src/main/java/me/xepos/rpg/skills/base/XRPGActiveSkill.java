package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public abstract class XRPGActiveSkill extends XRPGSkill{
    public XRPGActiveSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    public String getCooldownMessage(){
        return ChatColor.RED + getSkillName() + " is still on cooldown for " + (getRemainingCooldown() - System.currentTimeMillis()) / 1000 + " seconds";
    }
}
