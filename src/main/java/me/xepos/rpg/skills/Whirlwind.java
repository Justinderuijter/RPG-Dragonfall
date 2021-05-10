package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class Whirlwind extends XRPGSkill {
    public Whirlwind(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_DEALT").addSkill(this);
    }

    @Override
    @SuppressWarnings("all")
    public void activate(Event event) {
        if(!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        if (!isSkillReady()){
            e.getDamager().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        Player caster = (Player) e.getDamager();
        List<LivingEntity> targets = new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), 10, 3, 10, p -> p instanceof LivingEntity && p != caster && p != e.getEntity()));
        for (LivingEntity target:targets) {
            target.damage(getDamage(), caster);
        }
    }

    @Override
    public void initialize() {

    }
}
