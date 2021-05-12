package me.xepos.rpg.handlers;

import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ShootBowEventHandler extends EventHandler {
    private byte currentIndex = 0;
    private HashMap<String, XRPGSkill> passiveSkills = new HashMap<>();

    public void next() {
        if (!getSkills().isEmpty()) {
            currentIndex++;
            if (currentIndex > getSkills().size() - 1) {
                currentIndex = 0;
            }
        }
    }

    public XRPGSkill getCurrentSkill() {
        return (XRPGSkill) getSkills().values().toArray()[currentIndex];
    }

    @Override
    public void clear() {
        super.clear();
        currentIndex = 0;
    }

    @Override
    public void invoke(Event e) {
        for (XRPGSkill passiveSkill : passiveSkills.values()) {
            passiveSkill.activate(e);
        }

        if (getSkills().size() > 0) {
            getCurrentSkill().activate(e);
        }
    }

    public HashMap<String, XRPGSkill> getPassiveSkills() {
        return passiveSkills;
    }

    public void setPassiveSkills(HashMap<String, XRPGSkill> skills) {
        this.passiveSkills = skills;
    }

    public void addPassiveSkill(String skillId, XRPGSkill skill) {
        if (!passiveSkills.containsKey(skillId))
            passiveSkills.put(skillId, skill);
    }

    public void removePassiveSkill(String skillId) {
        passiveSkills.remove(skillId);
    }
}
