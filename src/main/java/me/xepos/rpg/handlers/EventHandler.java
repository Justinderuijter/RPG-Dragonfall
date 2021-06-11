package me.xepos.rpg.handlers;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

import java.util.HashMap;

public abstract class EventHandler {
    private final XRPGPlayer xrpgPlayer;

    public EventHandler(XRPGPlayer xrpgPlayer){
        this.xrpgPlayer = xrpgPlayer;
    }

    public EventHandler(){
        this.xrpgPlayer = null;
    }

    private HashMap<String, XRPGSkill> skills = new HashMap<>();

    public HashMap<String, XRPGSkill> getSkills() {
        return skills;
    }

    public void setSkills(HashMap<String, XRPGSkill> skills) {
        this.skills = skills;
    }

    public void addSkill(String skillId, XRPGSkill skill) {
        if (!skills.containsKey(skillId)) {
            skills.put(skillId, skill);
        }
    }

    public void removeSkill(String skillId) {
        skills.remove(skillId);
    }

    public abstract void invoke(Event e);

    public abstract void initialize();

    public boolean containsSkill(XRPGSkill skill) {
        return skills.values().stream().anyMatch(skill.getClass()::isInstance);
    }

    public void clear() {
        skills.clear();
    }

    protected XRPGPlayer getXRPGPlayer(){
        return xrpgPlayer;
    }
}
