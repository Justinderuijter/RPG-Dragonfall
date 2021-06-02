package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class Blessed extends XRPGPassiveSkill {
    public Blessed(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        if (!xrpgPlayer.getPassiveHandlerList().containsKey("ATTRIBUTE")){
            xrpgPlayer.addPassiveEventHandler("ATTRIBUTE", new PassiveEventHandler());
        }

        xrpgPlayer.getPassiveEventHandler("ATTRIBUTE").addSkill(this.getClass().getSimpleName(), this);

        xrpgPlayer.setMaximumMana(xrpgPlayer.getMaximumMana() + getSkillVariables().getInt("mana-increase", 5));
    }

    @Override
    public void activate(Event event) {

    }

    @Override
    public void initialize() {

    }
}
