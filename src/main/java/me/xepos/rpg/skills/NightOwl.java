package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffectType;

public class NightOwl extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.BUFF};

    public NightOwl(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if(!(event instanceof PlayerItemHeldEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

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
