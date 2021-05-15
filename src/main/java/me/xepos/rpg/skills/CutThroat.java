package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class CutThroat extends XRPGActiveSkill {

    public CutThroat(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        LivingEntity entity = (LivingEntity) e.getEntity();

        if (e.getEntity() instanceof LivingEntity) {
            Vector attackerDirection = e.getDamager().getLocation().getDirection();
            Vector victimDirection = e.getEntity().getLocation().getDirection();
            //determine if the dot product between the vectors is greater than 0
            //If it is, we can conclude that the attack was a backstab
            if (attackerDirection.dot(victimDirection) > 0) {
                if (entity.getHealth() <= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / (100 / getSkillVariables().getDouble("threshold", 25.0)) && Utils.isSkillReady(getRemainingCooldown())) {
                    entity.setHealth(0.0);
                    if (entity instanceof Player) {
                        e.getDamager().getWorld().getNearbyEntities(e.getDamager().getLocation(), 10, 5, 10, p -> p instanceof Player).forEach(p -> p.sendMessage(entity.getName() + " was executed by " + e.getDamager().getName() + "!"));
                    }
                    setRemainingCooldown(getCooldown());

                } else {
                    double finalDmg = e.getDamage() * getDamageMultiplier();
                    e.setDamage(finalDmg);
                    e.getDamager().sendMessage("Backstrike dealt " + finalDmg + " damage!");
                }
            }
        }

    }

    @Override
    public void initialize() {

    }
}
