package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.FlamethrowerTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitTask;

public class Flamethrower extends XRPGActiveSkill {
    private BukkitTask flamethrowerTask;

    public Flamethrower(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        this.flamethrowerTask = null;
        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemHeldEvent e) {
            if (!isInUse()) {
                doTrailOfFlames(e.getPlayer());
            }else{
                flamethrowerTask.cancel();
            }
        }

    }

    @Override
    public void initialize() {

    }

    private void doTrailOfFlames(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        } else if (!hasRequiredMana()) {
            sendNotEnoughManaMessage();
            return;
        }

        updatedCasterMana();
        final int interval = getSkillVariables().getInt(getSkillLevel(), "interval", 4);
        final double inaccuracy = getSkillVariables().getDouble(getSkillLevel(), "inaccuracy", 1.5);
        flamethrowerTask = new FlamethrowerTask(getXRPGPlayer(), this, getSkillVariables().getDamage(getSkillLevel()), inaccuracy, interval).runTaskTimer(getPlugin(), 0, interval);

    }

    public boolean isInUse() {
        return flamethrowerTask != null && !flamethrowerTask.isCancelled();
    }
}
