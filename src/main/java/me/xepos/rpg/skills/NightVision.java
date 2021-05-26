package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVision extends XRPGActiveSkill {
    public NightVision(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if(!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
        Player caster = e.getPlayer();

        if (caster.hasPotionEffect(PotionEffectType.NIGHT_VISION)){
            caster.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }else{
            int duration = getSkillVariables().getInt("duration", -1);
            if (duration == -1) duration = Integer.MAX_VALUE;

            caster.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 0, false, false, false));
        }

    }

    @Override
    public void initialize() {

    }
}
