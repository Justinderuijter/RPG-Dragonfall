package me.xepos.rpg.handlers;

import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

import java.util.HashMap;

public class EventHandler {
    private HashMap<String, XRPGSkill> skills = new HashMap<>();

    public HashMap<String, XRPGSkill> getSkills() {
        return skills;
    }

    public void setSkills(HashMap<String, XRPGSkill> skills) {
        this.skills = skills;
    }

    public void addSkill(String skillId, XRPGSkill skill) {
        if (!skills.containsKey(skillId))
            skills.put(skillId, skill);
    }

    public void removeSkill(String skillId) {
        skills.remove(skillId);
    }

    public void invoke(Event e) {
        for (XRPGSkill skill : skills.values()) {
            skill.activate(e);
        }
    }

    public void initialize() {
        for (XRPGSkill skill : skills.values()) {
            skill.initialize();
        }
    }

    public boolean containsSkill(XRPGSkill skill) {
        return skills.values().stream().anyMatch(skill.getClass()::isInstance);
    }

    public void clear() {
        skills.clear();
    }

}
