package me.xepos.rpg.handlers;

import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;

import java.util.HashMap;

public class EventHandler {
    private byte currentIndex = 0;
    private HashMap<String, XRPGSkill> passiveSkills = new HashMap<>();
    private HashMap<String, XRPGSkill> skills = new HashMap<>();

    public HashMap<String, XRPGSkill> getSkills() {
        return skills;
    }

    ////////////////////
    //                //
    //     skills     //
    //                //
    ////////////////////

    public void setSkills(HashMap<String, XRPGSkill> skills) {
        this.skills = skills;
    }

    public void addSkill(String skillId, XRPGSkill skill) {
        if (!skills.containsKey(skillId))
            skills.put(skillId, skill);
    }

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

    public void removeSkill(String skillId) {
        skills.remove(skillId);
    }

    public void invoke(Event e) {
        for (XRPGSkill skill: passiveSkills.values()) {
            skill.activate(e);
        }

        if (getSkills().size() > 0) {
            getCurrentSkill().activate(e);
        }
    }

    public void initialize() {
        for (XRPGSkill skill : skills.values()) {
            skill.initialize();
        }
        for (XRPGSkill skill : passiveSkills.values()){
            skill.initialize();
        }
    }

    public boolean containsSkill(XRPGSkill skill) {
        return skills.values().stream().anyMatch(skill.getClass()::isInstance);
    }

    public void clear() {
        skills.clear();
    }

    ////////////////////
    //                //
    // Passive Skills //
    //                //
    ////////////////////

    public void setPassiveSkills(HashMap<String, XRPGSkill> passiveSkills) {
        this.passiveSkills = skills;
    }

    public void addPassiveSkill(String skillId, XRPGSkill passiveSkill) {
        if (!passiveSkills.containsKey(skillId))
            passiveSkills.put(skillId, passiveSkill);
    }


    public void removePassiveSkill(String skillId) {
        passiveSkills.remove(skillId);
    }

    public boolean containsPassiveSkill(String skillId) {
        return passiveSkills.containsKey(skillId);
    }


}
