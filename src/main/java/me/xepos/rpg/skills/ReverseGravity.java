package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.LeapOfFaithTask;
import me.xepos.rpg.tasks.particles.ParticleSpiralEffectTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class ReverseGravity extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DISPLACEMENT, SpellType.BUFF, SpellType.DEBUFF};


    public ReverseGravity(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

        if (!isSkillReady()){
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double buffDuration = getSkillVariables().getDouble(getSkillLevel(), "buff-duration", 5.0);
        final int areaDuration = getSkillVariables().getInt(getSkillLevel(), "area-duration", 5);

        new LeapOfFaithTask(e.getPlayer().getLocation(), areaDuration, buffDuration).runTaskTimer(getPlugin(), 20L, 20L);
        new ParticleSpiralEffectTask(Particle.FIREWORKS_SPARK, e.getPlayer(), 2.5, 1, areaDuration, false, 0.02).runTaskTimer(getPlugin(), 20L, 1L);

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
