package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GoldenAppleAoE extends XRPGSkill {
    private EnchantedGoldenAppleAoE GAppleAoE;

    public GoldenAppleAoE(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, EnchantedGoldenAppleAoE GAppleAoE) {
        super(xrpgPlayer, skillVariables, plugin);

        this.GAppleAoE = GAppleAoE;
        xrpgPlayer.getEventHandler("CONSUME_ITEM").addSkill(this);
    }

    public GoldenAppleAoE(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("CONSUME_ITEM").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemConsumeEvent)) return;
        PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;
        if (GAppleAoE != null) {
            if (!isSkillReady() || !GAppleAoE.isSkillReady()) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), Math.max(GAppleAoE.getRemainingCooldown(), getRemainingCooldown())));
                e.setCancelled(true);
                return;
            }
        } else {
            if (!isSkillReady()) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                e.setCancelled(true);
                return;
            }
        }

        final double xRange = getSkillVariables().getDouble("x-range", 10.0);
        final double yRange = getSkillVariables().getDouble("y-range", 5.0);
        final double zRange = getSkillVariables().getDouble("z-range", xRange);

        List<PotionEffect> potionEffects = new ArrayList<PotionEffect>() {{
            add(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
            add(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        }};

        Utils.addPotionEffects(getNearbyAlliedPlayers(e.getPlayer(), xRange, yRange, zRange), potionEffects);

        setRemainingCooldown(getCooldown());

    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getEventHandler("CONSUME_ITEM").getSkills()) {
            if (skill instanceof EnchantedGoldenAppleAoE) {
                this.GAppleAoE = (EnchantedGoldenAppleAoE) skill;
                return;
            }
        }
    }

    @Override
    public boolean isSkillReady() {
        if (GAppleAoE != null) {
            return getRemainingCooldown() > System.currentTimeMillis() && GAppleAoE.getRemainingCooldown() > System.currentTimeMillis();
        }
        return super.getRemainingCooldown() > System.currentTimeMillis();
    }
}
