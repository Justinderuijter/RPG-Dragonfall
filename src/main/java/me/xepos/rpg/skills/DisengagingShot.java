package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class DisengagingShot extends XRPGBowSkill {
    public DisengagingShot(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;

        if (e.getProjectile() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getProjectile();

            ProjectileData data = new ProjectileData(arrow, 10);
            data.setDisengage(true);
            getPlugin().projectiles.put(e.getProjectile().getUniqueId(), data);

            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                getPlugin().projectiles.remove(arrow.getUniqueId());
            }, (long)(getSkillVariables().getDouble("max-travel-time", 1.0) * 20));
        }
    }

    @Override
    public void initialize() {

    }
}
