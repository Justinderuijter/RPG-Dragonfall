package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AceUpMySleeve extends XRPGPassiveSkill {

    public AceUpMySleeve(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("SHOOT_BOW").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;

        if (e.getProjectile() instanceof Arrow){
            Arrow arrow = (Arrow) e.getProjectile();

            List<String> enchants = getSkillVariables().getStringList("enchantments");
            String enchant = enchants.get(ThreadLocalRandom.current().nextInt(enchants.size()));

            //TODO: Add effects, awaiting information
            switch(enchant.toUpperCase()){
                case "POWER":
                    arrow.setDamage(arrow.getDamage() * 1.5);
                    break;
                case "FIRE":
                case "FLAME":
                    arrow.setFireTicks(arrow.getFireTicks() + 100);
                    break;
                case "PUNCH":
                case "KNOCKBACK":
                    arrow.setKnockbackStrength(arrow.getKnockbackStrength() + 1);
                    break;
                case "PIERCE":
                case "PIERCING":
                    arrow.setPierceLevel(arrow.getPierceLevel() + 1);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void initialize() {

    }
}
