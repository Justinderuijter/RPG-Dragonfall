package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class XRPGAttributeSkill extends XRPGPassiveSkill{
    public XRPGAttributeSkill(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);
    }

    @Override
    public void activate(Event event) {

    }

    @Override
    public void initialize() {

    }


}
