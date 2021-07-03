package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import org.bukkit.ChatColor;

public abstract class XRPGPassiveSkill extends XRPGSkill{
    public XRPGPassiveSkill(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);
    }

    public String getCooldownMessage(){
        return ChatColor.RED + getSkillName() + " is now on cooldown for " + getCooldown() + " seconds";
    }
}
