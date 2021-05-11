package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class Blessed extends XRPGSkill {
    public Blessed(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("HEALTH_REGEN");
        xrpgPlayer.setMaximumMana(xrpgPlayer.getMaximumMana() + getSkillVariables().getInt("mana-increase", 5));
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
