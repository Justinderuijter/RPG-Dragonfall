package me.xepos.rpg.handlers;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class ActiveEventHandler extends EventHandler{
    private BowEventHandler bowEventHandler = null;

    public ActiveEventHandler(XRPGPlayer xrpgPlayer){
        super(xrpgPlayer);
    }

    @Override
    public void removeSkill(String skillId) {
        getSkills().remove(skillId);
    }

    public void invoke(Event e) {
        if (!(e instanceof PlayerItemHeldEvent)) return;
        if (bowEventHandler == null){
            getXRPGPlayer().getPlayer().sendMessage("BowHandler linked!");
            bowEventHandler = (BowEventHandler) getXRPGPlayer().getPassiveEventHandler("SHOOT_BOW");
        }

        final int slot = ((PlayerItemHeldEvent)e).getNewSlot();
        if(getXRPGPlayer().getSpellKeybinds().size() > slot && slot < 7) {

            if (getSkills().get(getXRPGPlayer().getSkillForSlot(slot)) instanceof XRPGBowSkill){
                Bukkit.getLogger().info("Triggered: " + getXRPGPlayer().getSkillForSlot(slot));

                bowEventHandler.setActiveBowSkill(getXRPGPlayer().getSkillForSlot(slot));
            }else{
                getSkills().get(getXRPGPlayer().getSkillForSlot(slot)).activate(e);
            }
        }
    }

    @Override
    public void initialize() {

    }

    public boolean containsSkill(XRPGSkill skill) {
        return getSkills().values().stream().anyMatch(skill.getClass()::isInstance);
    }

    @Override
    public void clear() {
        getSkills().clear();
    }
}
