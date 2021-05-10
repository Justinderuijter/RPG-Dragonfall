package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tasks.RavagerLandTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class SoaringSlash extends XRPGSkill {
    BukkitTask landTask = null;

    public SoaringSlash(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;

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
