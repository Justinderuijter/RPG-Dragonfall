package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tasks.HealOverTimeTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Ballad extends XRPGActiveSkill {


    public Ballad(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        Player caster = e.getPlayer();

        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        final double xRange = getSkillVariables().getDouble("x-range", 10);
        final double yRange = getSkillVariables().getDouble("y-range", 5);
        final double zRange = getSkillVariables().getDouble("z-range", xRange);

        List<Player> nearbyPlayers = getNearbyAlliedPlayers(caster, xRange, yRange, zRange);
        for (Player nearbyPlayer : nearbyPlayers) {
            new HealOverTimeTask(nearbyPlayer, getSkillVariables().getDouble("heal-per-proc", 1.0), getSkillVariables().getInt("max-procs", 10)).runTaskTimer(getPlugin(), 1L, (long) getSkillVariables().getDouble("interval", 1.0) * 20L);
        }

        setRemainingCooldown(getCooldown());
    }

    @Override
    public void initialize() {

    }

}
