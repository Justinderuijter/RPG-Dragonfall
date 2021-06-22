package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EnchantedGoldenAppleAoE extends XRPGPassiveSkill {
    public EnchantedGoldenAppleAoE(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("CONSUME_ITEM").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemConsumeEvent)) return;
        PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;

        if (!Utils.isSkillReady(getRemainingCooldown())) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            e.setCancelled(true);
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double xRange = getSkillVariables().getDouble(getSkillLevel(), "x-range", 10.0);
        final double yRange = getSkillVariables().getDouble(getSkillLevel(), "y-range", 5.0);
        final double zRange = getSkillVariables().getDouble(getSkillLevel(), "z-range", xRange);

        List<PotionEffect> potionEffects = new ArrayList<PotionEffect>() {{
            add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
            add(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
            add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
            add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));
        }};

        Utils.addPotionEffects(getNearbyAlliedPlayers(e.getPlayer(), xRange, yRange, zRange), potionEffects);

        setRemainingCooldown(getCooldown());
        updatedCasterMana();

    }

    @Override
    public void initialize() {

    }
}
