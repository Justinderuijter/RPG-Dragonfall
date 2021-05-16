package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class DisengagingShot extends XRPGActiveSkill {
    public DisengagingShot(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        throw new NotImplementedException("Disengaging shot is not yet implemented");
    }

    @Override
    public void initialize() {

    }
}
