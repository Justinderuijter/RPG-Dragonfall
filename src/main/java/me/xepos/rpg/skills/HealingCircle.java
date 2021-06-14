package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.HealOverTimeTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.List;

public class HealingCircle extends XRPGActiveSkill {


    public HealingCircle(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
        Player caster = e.getPlayer();

        if (!isSkillReady()) {
            caster.sendMessage(getCooldownMessage());
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double xRange = getSkillVariables().getDouble("x-range", 10);
        final double yRange = getSkillVariables().getDouble("y-range", 5);
        final double zRange = getSkillVariables().getDouble("z-range", xRange);

        List<Player> nearbyPlayers = getNearbyAlliedPlayers(caster, xRange, yRange, zRange);
        nearbyPlayers.removeIf(p -> !canApplyBuffToFriendly(p));
        for (Player nearbyPlayer : nearbyPlayers) {
            new HealOverTimeTask(nearbyPlayer, getSkillVariables().getDouble("heal-per-proc", 1.0), getSkillVariables().getInt("max-procs", 10)).runTaskTimer(getPlugin(), 1L, (long) getSkillVariables().getDouble("interval", 1.0) * 20L);
        }

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }

}
