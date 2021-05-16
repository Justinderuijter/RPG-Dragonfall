package me.xepos.rpg.handlers;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.HashMap;

public class ActiveEventHandler implements IEventHandler{
    private HashMap<String, XRPGSkill> skills = new HashMap<>();
    private final XRPGPlayer xrpgPlayer;

    public ActiveEventHandler(XRPGPlayer xrpgPlayer){
        this.xrpgPlayer = xrpgPlayer;
    }

    public HashMap<String, XRPGSkill> getSkills() {
        return skills;
    }

    public void setSkills(HashMap<String, XRPGSkill> skills) {
        this.skills = skills;
    }

    public void addSkill(String skillId, XRPGActiveSkill skill) {
        if (!skills.containsKey(skillId)) {
            skills.put(skillId, skill);
            xrpgPlayer.getSpellKeybinds().add(skillId);
        }
    }

    @Override
    public void removeSkill(String skillId) {
        skills.remove(skillId);
    }

    public void invoke(PlayerItemHeldEvent e) {
        final int slot = e.getNewSlot();
        if(xrpgPlayer.getSpellKeybinds().size() > slot && slot < 7) {
            skills.get(xrpgPlayer.getSkillForSlot(slot)).activate(e);
        }
    }

    @Override
    public void initialize() {

    }

    public boolean containsSkill(XRPGSkill skill) {
        return skills.values().stream().anyMatch(skill.getClass()::isInstance);
    }

    @Override
    public void clear() {
        skills.clear();
    }
}
