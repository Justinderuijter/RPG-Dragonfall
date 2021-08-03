package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.BeamTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitTask;

public class HealingBeam extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DAMAGE, SpellType.HEAL};
    BukkitTask beamTask;

    public HealingBeam(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        this.beamTask = null;
        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
        if (!xrpgPlayer.getPassiveHandlerList().containsKey("XRPG_SPELL_CAST")){
            xrpgPlayer.getPassiveHandlerList().put("XRPG_SPELL_CAST", new PassiveEventHandler());
        }

        xrpgPlayer.getPassiveEventHandler("XRPG_SPELL_CAST").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemHeldEvent e) {
            if (!isInUse()) {
                XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
                Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

                if (spellCastEvent.isCancelled()) return;

                doHealingBeam(e.getPlayer());
            }else{
                beamTask.cancel();
            }
        }else if (event instanceof XRPGSpellCastEvent e){
            if (isInUse()){
                e.setCancelled(true);
            }
        }
    }

    @Override
    public void initialize() {

    }

    private void doHealingBeam(Player caster){
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        } else if (!hasRequiredMana()) {
            sendNotEnoughManaMessage();
            return;
        }

        updatedCasterMana();
        setRemainingCooldown(getCooldown());

        final double range = getSkillVariables().getDouble(getSkillLevel(), "range", 8);
        final double heal = getSkillVariables().getDouble(getSkillLevel(), "heal-per-tick", 2.0);
        final double damage = getSkillVariables().getDouble(getSkillLevel(), "damage-per-tick", 2.0);
        final int interval = getSkillVariables().getInt(getSkillLevel(), "tick-delay", 5);

        beamTask = new BeamTask(caster, this, heal, damage, range, interval, getPartySet()).runTaskTimer(getPlugin(), 0, interval);
    }

    public boolean isInUse() {
        return beamTask != null && !beamTask.isCancelled();
    }
}
