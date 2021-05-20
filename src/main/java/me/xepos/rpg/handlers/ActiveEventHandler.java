package me.xepos.rpg.handlers;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.List;

public class ActiveEventHandler extends EventHandler{
    private BowEventHandler bowEventHandler = null;

    public ActiveEventHandler(XRPGPlayer xrpgPlayer){
        super(xrpgPlayer);

        bowEventHandler = (BowEventHandler) getXRPGPlayer().getPassiveEventHandler("SHOOT_BOW");
    }

    @Override
    public void removeSkill(String skillId) {
        getSkills().remove(skillId);
    }

    public void invoke(Event e) {
        if (!(e instanceof PlayerItemHeldEvent)) return;
        if (bowEventHandler == null){
            List<String> error = new ArrayList<String>(){{
                add(ChatColor.RED + "Something went wrong while executing this skill.");
                add(ChatColor.RED + "Please report this bug to your server administrator");
            }};
            for (String string:error) {
                getXRPGPlayer().getPlayer().sendMessage(string);
            }

        }

        final int slot = ((PlayerItemHeldEvent)e).getNewSlot();
        if(getXRPGPlayer().getSpellKeybinds().size() > slot && slot < 7) {

            if (getSkills().get(getXRPGPlayer().getSkillForSlot(slot)) instanceof XRPGBowSkill){
                getXRPGPlayer().getPlayer().sendMessage(ChatColor.DARK_GREEN + "You loaded a special arrow into your bow");
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
