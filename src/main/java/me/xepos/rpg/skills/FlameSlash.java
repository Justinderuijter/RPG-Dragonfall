package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class FlameSlash extends XRPGActiveSkill {
    public FlameSlash(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doFlameSlash(e);
    }

    @Override
    public void initialize() {

    }

    private void doFlameSlash(PlayerItemHeldEvent e) {

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
