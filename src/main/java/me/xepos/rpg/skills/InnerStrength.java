package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InnerStrength extends XRPGActiveSkill {
    private LotusStrike lotusStrike;
    private final double potionDuration = getSkillVariables().getDouble("duration", 6.0);

    private final List<PotionEffect> defEffects = new ArrayList<PotionEffect>() {{
        add(new PotionEffect(PotionEffectType.REGENERATION, (int) (potionDuration * 20), 1, false, false, true));
        add(new PotionEffect(PotionEffectType.SLOW_FALLING, (int) (potionDuration * 20), 0, false, false, true));
        add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (int) (potionDuration * 20), 0, false, false, true));
        add(new PotionEffect(PotionEffectType.ABSORPTION, (int) (potionDuration * 20), 0, false, false, true));
    }};

    public InnerStrength(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel, LotusStrike lotusStrike) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        this.lotusStrike = lotusStrike;

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    public InnerStrength(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemHeldEvent) {
            PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
            if (lotusStrike == null){
                useInnerStrength(e.getPlayer());
            }else if (lotusStrike.canUseLotus(e.getPlayer())) {
                useInnerStrength(e.getPlayer());
            }
        }
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getPassiveEventHandler("DAMAGE_DEALT").getSkills().values()) {
            if (skill instanceof LotusStrike) {
                this.lotusStrike = (LotusStrike) skill;
                return;
            }
        }
    }

    private void useInnerStrength(Player player) {
        final double healAmount = getSkillVariables().getDouble("heal", 3.0);

        applyTriggerEffect(player);
        Utils.healLivingEntity(player, healAmount);
        player.sendMessage("Inner Strength healed you for " + healAmount);
    }

    private void applyTriggerEffect(Player player) {
        Random rand = new Random();

        player.addPotionEffect(defEffects.get(rand.nextInt(defEffects.size())));

        if (lotusStrike != null)
            lotusStrike.incrementHitCount();
    }

}
