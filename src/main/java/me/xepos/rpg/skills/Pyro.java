package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class Pyro extends XRPGPassiveSkill {
    public Pyro(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN_ENVIRONMENTAL").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityDamageEvent){
            EntityDamageEvent e = (EntityDamageEvent) event;
            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                    e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                    e.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR ||
                    e.getCause() == EntityDamageEvent.DamageCause.LAVA){

                double damageReduction = 100 / getSkillVariables().getDouble("damage-reduction", 50);
                e.setDamage(e.getDamage() / damageReduction);
            }

        }
    }

    @Override
    public void initialize() {

    }
}
