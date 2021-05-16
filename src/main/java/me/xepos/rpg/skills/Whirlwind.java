package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.List;

public class Whirlwind extends XRPGActiveSkill {
    public Whirlwind(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    @SuppressWarnings("all")
    public void activate(Event event) {
        if(!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        if (!isSkillReady()){
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        Player caster = e.getPlayer();
        List<LivingEntity> targets = new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), 10, 3, 10, p -> p instanceof LivingEntity && p != caster));
        for (LivingEntity target:targets) {
            target.damage(getDamage(), caster);
        }
    }

    @Override
    public void initialize() {

    }
}
