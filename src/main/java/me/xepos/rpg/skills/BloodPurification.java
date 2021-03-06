package me.xepos.rpg.skills;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BloodPurification extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.AOECLEANSE};

    private final static List<PotionEffectType> potionEffectTypes = new ArrayList<>() {{
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

    public BloodPurification(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

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
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double xRange = getSkillVariables().getDouble(getSkillLevel(), "x-range", 10.0);
        final double yRange = getSkillVariables().getDouble(getSkillLevel(), "y-range", 5.0);
        final double zRange = getSkillVariables().getDouble(getSkillLevel(), "z-range", xRange);

        final List<LivingEntity> livingEntities = new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), xRange, yRange, zRange, p -> p instanceof LivingEntity));
        final HashMap<String, AttributeModifierData> modifierData = AttributeModifierManager.getInstance().getModifiers(ModifierType.NEGATIVE);

        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity instanceof Player) {
                Player target = (Player) livingEntity;
                if (getProtectionSet().isPvPTypeSame(caster.getLocation(), target.getLocation()) && getPartySet().isPlayerAllied(caster, target)) {
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
        updatedCasterMana();
    }

    private void cleanseBadPotionEffects(LivingEntity livingTarget) {

        for (PotionEffectType potionEffectType : potionEffectTypes) {
            if (livingTarget.hasPotionEffect(potionEffectType)) {
                livingTarget.removePotionEffect(potionEffectType);
            }
        }
    }

}
