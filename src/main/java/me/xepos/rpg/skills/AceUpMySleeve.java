package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AceUpMySleeve extends XRPGPassiveSkill {

    public AceUpMySleeve(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("SHOOT_BOW").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;

        if (getSkillVariables().getInt(getSkillLevel(), "trigger-chance") >= ThreadLocalRandom.current().nextInt(100)) return;

        if (e.getProjectile() instanceof Arrow){
            Arrow arrow = (Arrow) e.getProjectile();

            List<String> enchants = getSkillVariables().getStringList(getSkillLevel(), "enchantments");
            String enchant = enchants.get(ThreadLocalRandom.current().nextInt(enchants.size()));

            switch(enchant.toUpperCase()){
                case "POWER":
                    arrow.setDamage(arrow.getDamage() * 1.5);
                    break;
                case "FIRE":
                case "FLAME":
                    //Need to check if this does anything
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> arrow.setFireTicks(arrow.getFireTicks() + 100), 1);
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
