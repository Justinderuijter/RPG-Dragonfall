package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.Vector;

public class LeapOfFaith extends XRPGActiveSkill {
    public LeapOfFaith(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        if (!isSkillReady()){
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final int velocity = getSkillVariables().getInt(getSkillLevel(), "velocity", 5);

        e.getPlayer().setVelocity(e.getPlayer().getEyeLocation().getDirection().multiply(new Vector(velocity, 0, velocity)));

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
