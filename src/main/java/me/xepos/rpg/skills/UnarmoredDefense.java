package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class UnarmoredDefense extends XRPGPassiveSkill {
    public UnarmoredDefense(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("TEMPHANDLER").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {

    }

    @Override
    public void initialize() {

    }
}
