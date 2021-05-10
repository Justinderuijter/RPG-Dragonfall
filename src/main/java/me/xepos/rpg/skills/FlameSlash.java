package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class FlameSlash extends XRPGSkill {
    public FlameSlash(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;

        doFlameSlash(e);
    }

    @Override
    public void initialize() {

    }

    private void doFlameSlash(PlayerInteractEvent e) {

        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        Fireball fireball = e.getPlayer().launchProjectile(SmallFireball.class);

        if (!getPlugin().projectiles.containsKey(fireball.getUniqueId())) {
            ProjectileData projectileData = new ProjectileData(fireball, getDamage(), 10);
            projectileData.setShouldBounce(true);

            getPlugin().projectiles.put(fireball.getUniqueId(), projectileData);
        }

        setRemainingCooldown(getCooldown());

    }
}
