package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class RapidFire extends XRPGActiveSkill {
    public RapidFire(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;

        Arrow arrow = (Arrow) e.getProjectile();
        double damage = arrow.getDamage();
        ProjectileData data = new ProjectileData(arrow, damage, 20);
        getPlugin().projectiles.put(arrow.getUniqueId(), data);

        for (int i = 0; i < getSkillVariables().getInt("extra-arrows"); i++) {
            Arrow extraArrow = e.getEntity().launchProjectile(Arrow.class, e.getProjectile().getLocation().getDirection());
            extraArrow.setDamage(0);

            ProjectileData extraData = new ProjectileData(extraArrow, damage, 20);
            getPlugin().projectiles.put(extraArrow.getUniqueId(), extraData);
        }



    }

    @Override
    public void initialize() {

    }
}
