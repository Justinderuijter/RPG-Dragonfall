package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class HotHead extends XRPGPassiveSkill {
    private static final Set<EntityDamageEvent.DamageCause> causes = new HashSet<EntityDamageEvent.DamageCause>(){{
        add(EntityDamageEvent.DamageCause.FIRE);
        add(EntityDamageEvent.DamageCause.FIRE_TICK);
        add(EntityDamageEvent.DamageCause.HOT_FLOOR);
        add(EntityDamageEvent.DamageCause.LAVA);
    }};

    public HotHead(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

            if (ThreadLocalRandom.current().nextInt(100) < 25){
                if (e.getDamager() instanceof LivingEntity) {
                    e.getDamager().setFireTicks(100);
                }
            }
        }else if(event instanceof EntityDamageEvent){
            EntityDamageEvent e = (EntityDamageEvent) event;

            if (causes.contains(e.getCause())){
                e.setDamage(e.getDamage() * (1 - getSkillVariables().getDouble(getSkillLevel(), "fire-damage-reduction-multiplier", 0.5)));
            }
        }
    }

    @Override
    public void initialize() {

    }
}
