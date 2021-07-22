package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.concurrent.ThreadLocalRandom;

public class Bullseye extends XRPGPassiveSkill {

    public Bullseye(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        setRemainingCooldown(-1);
        xrpgPlayer.getPassiveEventHandler("SHOOT_BOW").addSkill(this.getClass().getSimpleName(),this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent e)) return;
        if (e.getProjectile() instanceof Arrow) {
            int random = ThreadLocalRandom.current().nextInt(0, 100);
            if (random < getSkillVariables().getInt(getSkillLevel(), "activation-chance", 30)) {
                ((Arrow) e.getProjectile()).setCritical(true);
            }
        }


    }

    @Override
    public void initialize() {

    }
}
