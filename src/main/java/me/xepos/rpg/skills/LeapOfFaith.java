package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.LeapOfFaithTask;
import me.xepos.rpg.tasks.particles.ParticleSpiralEffectTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Particle;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

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

        new LeapOfFaithTask(e.getPlayer().getLocation()).runTaskTimer(getPlugin(), 20L, 20L);
        new ParticleSpiralEffectTask(Particle.FIREWORKS_SPARK, e.getPlayer(), 2.5, 1, 10, false, 0.02).runTaskTimer(getPlugin(), 20L, 1L);

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
