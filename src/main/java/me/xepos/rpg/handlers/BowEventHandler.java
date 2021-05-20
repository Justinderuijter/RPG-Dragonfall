package me.xepos.rpg.handlers;

import me.xepos.rpg.XRPGPlayer;
import org.bukkit.event.Event;

public class BowEventHandler extends PassiveEventHandler{
    private String activeBowSkill = null;

    public BowEventHandler(XRPGPlayer xrpgPlayer) {
        super(xrpgPlayer);
    }

    @Override
    public void invoke(Event e) {
        for (String skillId:getSkills().keySet()) {
            getSkills().get(skillId).activate(e);
        }
        if (activeBowSkill != null) {
            getXRPGPlayer().getActiveHandler().getSkills().get(activeBowSkill).activate(e);
            activeBowSkill = null;
        }
    }

    @Override
    public void initialize() {

    }

    public String getActiveBowSkill() {
        return activeBowSkill;
    }

    public void setActiveBowSkill(String activeBowSkill) {
        this.activeBowSkill = activeBowSkill;
    }
}
