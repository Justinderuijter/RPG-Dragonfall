package me.xepos.rpg.handlers;

import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class ShootBowEventHandler extends EventHandler {
    private byte currentIndex = 0;
    private List<XRPGSkill> passiveSkills = new ArrayList<>();

    public void next() {
        if (!getSkills().isEmpty()) {
            currentIndex++;
            if (currentIndex > getSkills().size() - 1) {
                currentIndex = 0;
            }
        }
    }

    public XRPGSkill getCurrentSkill() {
        return getSkills().get(currentIndex);
    }

    @Override
    public void clear() {
        super.clear();
        currentIndex = 0;
    }

    @Override
    public void invoke(Event e) {
        for (XRPGSkill passiveSkill : passiveSkills) {
            passiveSkill.activate(e);
        }

        if (getSkills().size() > 0) {
            getCurrentSkill().activate(e);
        }
    }

    public List<XRPGSkill> getPassiveSkills() {
        return passiveSkills;
    }

    public void setPassiveSkills(List<XRPGSkill> skills) {
        this.passiveSkills = skills;
    }

    public void addPassiveSkill(XRPGSkill skill) {
        if (!passiveSkills.contains(skill))
            passiveSkills.add(skill);
    }

    public void removePassiveSkill(XRPGSkill skill) {
        passiveSkills.remove(skill);
    }
}
