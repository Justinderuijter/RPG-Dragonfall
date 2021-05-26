package me.xepos.rpg.skills;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BloodPurification extends XRPGActiveSkill {

    private List<PotionEffectType> potionEffectTypes = new ArrayList<PotionEffectType>() {{
        add(PotionEffectType.CONFUSION);
        add(PotionEffectType.WITHER);
        add(PotionEffectType.WEAKNESS);
        add(PotionEffectType.SLOW_DIGGING);
        add(PotionEffectType.POISON);
        add(PotionEffectType.SLOW);
        add(PotionEffectType.HUNGER);
        add(PotionEffectType.HARM);
        add(PotionEffectType.BLINDNESS);
    }};

    public BloodPurification(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doBloodPurification(e.getPlayer());

    }

    @Override
    public void initialize() {

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void doBloodPurification(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        final double xRange = getSkillVariables().getDouble("x-range", 10.0);
        final double yRange = getSkillVariables().getDouble("y-range", 5.0);
        final double zRange = getSkillVariables().getDouble("z-range", xRange);

        final List<LivingEntity> livingEntities = new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), xRange, yRange, zRange, p -> p instanceof LivingEntity));
        final HashMap<String, AttributeModifierData> modifierData = AttributeModifierManager.getInstance().getModifiers(ModifierType.NEGATIVE);

        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity instanceof Player) {
                Player target = (Player) livingEntity;
                if (getProtectionSet().isPvPTypeSame(caster.getLocation(), target.getLocation()) && getPartyManager().isPlayerAllied(caster, target)) {
                    for (String key : modifierData.keySet()) {
                        Utils.removeUniqueModifier(target, modifierData.get(key));
                    }
                    cleanseBadPotionEffects(livingEntity);
                }
            } else {
                cleanseBadPotionEffects(livingEntity);
            }
        }

        setRemainingCooldown(getCooldown());
    }

    private void cleanseBadPotionEffects(LivingEntity livingTarget) {

        for (PotionEffectType potionEffectType : potionEffectTypes) {
            if (livingTarget.hasPotionEffect(potionEffectType)) {
                livingTarget.removePotionEffect(potionEffectType);
            }
        }
    }

}
