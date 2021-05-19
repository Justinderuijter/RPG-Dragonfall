package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.RavagerLandTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class SoaringSlash extends XRPGActiveSkill {
    BukkitTask landTask = null;

    public SoaringSlash(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        e.getPlayer().setVelocity(e.getPlayer().getEyeLocation().getDirection().multiply(new Vector(2, 0, 2)).add(new Vector(0, 1, 0)));
        setRemainingCooldown(getCooldown());
        if (landTask == null || landTask.isCancelled())
            landTask = new RavagerLandTask(e.getPlayer(), getDamage(), getProtectionSet(), getPartyManager()).runTaskTimer(getPlugin(), 5L, 3L);
    }

    @Override
    public void initialize() {

    }
}
