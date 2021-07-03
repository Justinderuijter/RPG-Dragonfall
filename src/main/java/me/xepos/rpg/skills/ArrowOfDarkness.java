package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArrowOfDarkness extends XRPGBowSkill {
    public ArrowOfDarkness(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;
        if (!(e.getProjectile() instanceof Arrow)) return;

        if (!isSkillReady()) {
            e.getEntity().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }
        Arrow arrow = (Arrow) e.getProjectile();

        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

        final int duration = (int) (getSkillVariables().getDouble(getSkillLevel(), "duration", 10.0) * 20);
        final int amplifier = getSkillVariables().getInt(getSkillLevel(), "amplifier", 1);

        ProjectileData data = new ProjectileData(arrow, 20, new PotionEffect(PotionEffectType.HARM, duration, amplifier, false, false, true));

        getPlugin().projectiles.put(arrow.getUniqueId(), data);

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
