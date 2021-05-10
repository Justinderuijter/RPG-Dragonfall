package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.configuration.ConfigurationSection;

public abstract class XRPGBowSkill extends XRPGSkill {
    public XRPGBowSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    public void enable() {

    }

    public void disable() {

    }
}
