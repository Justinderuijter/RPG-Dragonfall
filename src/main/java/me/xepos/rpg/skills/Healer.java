package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class Healer extends XRPGPassiveSkill {
    public Healer(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("HEALTH_REGEN").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if(!(event instanceof EntityRegainHealthEvent)) return;
        EntityRegainHealthEvent e = (EntityRegainHealthEvent) event;
        if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) return;

        e.setAmount(e.getAmount() * getSkillVariables().getDouble("multiplier", 1.25));
    }

    @Override
    public void initialize() {

    }
}
