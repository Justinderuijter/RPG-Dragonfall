package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public abstract class XRPGPassiveSkill extends XRPGSkill{
    public XRPGPassiveSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    public String getCooldownMessage(){
        return ChatColor.RED + getSkillName() + " is now on cooldown for " + getCooldown() + " seconds";
    }
}
