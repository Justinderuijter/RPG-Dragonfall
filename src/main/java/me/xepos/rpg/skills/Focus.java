package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.concurrent.ThreadLocalRandom;

public class Focus extends XRPGPassiveSkill {

    public Focus(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        setRemainingCooldown(-1);
        xrpgPlayer.getPassiveEventHandler("SHOOT_BOW").addSkill(this.getClass().getSimpleName(),this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;
        if (e.getProjectile() instanceof Arrow) {
            int random = ThreadLocalRandom.current().nextInt(0, 100);
            if (random < getSkillVariables().getInt("activation-chance", 30)) {
                ((Arrow) e.getProjectile()).setCritical(true);
            }
        }


    }

    @Override
    public void initialize() {

    }
}
