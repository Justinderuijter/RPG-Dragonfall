package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.HealOverTimeTask;
import me.xepos.rpg.tasks.particles.ParticleSpiralEffectTask;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.List;

public class HealingCircle extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.PASSIVE, SpellType.HOT};

    public HealingCircle(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

        Player caster = e.getPlayer();

        if (!isSkillReady()) {
            caster.sendMessage(getCooldownMessage());
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double xRange = getSkillVariables().getDouble(getSkillLevel(), "x-range", 10);
        final double yRange = getSkillVariables().getDouble(getSkillLevel(), "y-range", 5);

        List<Player> nearbyPlayers = getNearbyAlliedPlayers(caster, xRange, yRange, xRange);
        nearbyPlayers.removeIf(p -> !canApplyBuffToFriendly(p));
        new ParticleSpiralEffectTask(Particle.HEART, caster, xRange/2, 2,2, true, 1).runTaskTimer(getPlugin(), 0, 1);
        for (Player nearbyPlayer : nearbyPlayers) {
            new HealOverTimeTask(nearbyPlayer, getSkillVariables().getDouble(getSkillLevel(), "heal-per-proc", 1.0), getSkillVariables().getInt(getSkillLevel(), "max-procs", 10)).runTaskTimer(getPlugin(), 1L, (long) getSkillVariables().getDouble(getSkillLevel(), "interval", 1.0) * 20L);
        }

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }

}
