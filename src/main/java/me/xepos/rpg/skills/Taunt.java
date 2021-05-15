package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class Taunt extends XRPGSkill {
    public Taunt(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getDamager();

        final double xRange = getSkillVariables().getDouble("x-range", 16.0);
        final double yRange = getSkillVariables().getDouble("y-range", 5.0);
        final double zRange = getSkillVariables().getDouble("z-range", xRange);

        List<Entity> entities = e.getEntity().getNearbyEntities(xRange, yRange, zRange);
        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                player.sendMessage(entity.getName() + "is now targeting you!");
                ((Monster) entity).setTarget(player);
            }
        }
    }

    @Override
    public void initialize() {

    }
}
