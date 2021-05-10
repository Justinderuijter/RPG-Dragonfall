package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TrueDefender extends XRPGSkill {
    private boolean isActive = false;

    public TrueDefender(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getEntity();
        LivingEntity damager = (LivingEntity) e.getDamager();

        int duration = (int)(getSkillVariables().getDouble("duration", 3.0) * 20);

        player.setNoDamageTicks(duration);
        damager.setNoDamageTicks(0);
        damager.damage(e.getDamage(), player);

    }

    @Override
    public void initialize() {

    }
}
