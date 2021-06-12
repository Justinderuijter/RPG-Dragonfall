package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffectType;

public class NightOwl extends XRPGActiveSkill {
    public NightOwl(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if(!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
        Player caster = e.getPlayer();

        if (caster.hasPotionEffect(PotionEffectType.NIGHT_VISION)){
            getXRPGPlayer().removePermanentPotionEffect(PotionEffectType.NIGHT_VISION);
        }else{
            getXRPGPlayer().addPermanentPotionEffect(PotionEffectType.NIGHT_VISION, 9);
        }

    }

    @Override
    public void initialize() {

    }
}
