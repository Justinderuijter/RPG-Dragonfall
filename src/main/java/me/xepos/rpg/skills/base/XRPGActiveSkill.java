package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import org.bukkit.ChatColor;

public abstract class XRPGActiveSkill extends XRPGSkill{
    public XRPGActiveSkill(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);
    }

    public String getCooldownMessage(){
        return ChatColor.RED + getSkillName() + " is still on cooldown for " + (getRemainingCooldown() - System.currentTimeMillis()) / 1000 + " seconds";
    }
}
