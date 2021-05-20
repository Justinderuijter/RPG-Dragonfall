package me.xepos.rpg.handlers;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

public class PassiveEventHandler extends EventHandler{

    public PassiveEventHandler(XRPGPlayer xrpgPlayer) {
        super(xrpgPlayer);
    }

    public PassiveEventHandler() { }


    public void invoke(Event e) {
        for (XRPGSkill skill : getSkills().values()) {
            skill.activate(e);
        }

    }

    public void initialize() {
        for (XRPGSkill skill : getSkills().values()) {
            skill.initialize();
        }
    }
}