package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class Backstab extends XRPGPassiveSkill {
    public Backstab(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerToggleSneakEvent){
            PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
            if (e.getPlayer().isSneaking()){
                e.getPlayer().setInvisible(true);
            }else{
                e.getPlayer().setInvisible(false);
            }

        } else if(event instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            Vector attackerDirection = e.getDamager().getLocation().getDirection();
            Vector victimDirection = e.getEntity().getLocation().getDirection();
            //determine if the dot product between the vectors is greater than 0
            //If it is, we can conclude that the attack was a backstab
            if (attackerDirection.dot(victimDirection) > 0) {
                e.setDamage(e.getDamage() * getSkillVariables().getDouble("damage-multiplier"));
            }
        }
    }

    @Override
    public void initialize() {

    }
}
