package me.xepos.rpg.handlers;

import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    private List<XRPGSkill> skills = new ArrayList<>();

    public List<XRPGSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<XRPGSkill> skills) {
        this.skills = skills;
    }

    public void addSkill(XRPGSkill skill) {
        if (!skills.contains(skill))
            skills.add(skill);
    }

    public void removeSkill(XRPGSkill skill) {
        skills.remove(skill);
    }

    public void invoke(Event e) {
        for (XRPGSkill skill : skills) {
            skill.activate(e);
        }
    }

    public void initialize() {
        for (XRPGSkill skill : skills) {
            skill.initialize();
        }
    }

    public boolean containsSkill(XRPGSkill skill) {
        return skills.stream().anyMatch(skill.getClass()::isInstance);
    }

    public void clear() {
        skills.clear();
    }

}
