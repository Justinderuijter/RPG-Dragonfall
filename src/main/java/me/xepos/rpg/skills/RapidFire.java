package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

public class RapidFire extends XRPGBowSkill {
    public RapidFire(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;

        Arrow arrow = (Arrow) e.getProjectile();
        double damage = arrow.getDamage();
        final Vector velocity = arrow.getVelocity();

        arrow.setDamage(0);

        ProjectileData data = new ProjectileData(arrow, damage, 20);
        getPlugin().projectiles.put(arrow.getUniqueId(), data);

        for (int i = 0; i < getSkillVariables().getInt("extra-arrows"); i++) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                Arrow extraArrow = e.getEntity().launchProjectile(Arrow.class, e.getProjectile().getLocation().getDirection());
                extraArrow.setVelocity(velocity);
                extraArrow.setDamage(0);
                extraArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

                ProjectileData extraData = new ProjectileData(extraArrow, damage, 20);
                getPlugin().projectiles.put(extraArrow.getUniqueId(), extraData);
            }, (i + 1) * 5L);

        }



    }

    @Override
    public void initialize() {

    }
}
