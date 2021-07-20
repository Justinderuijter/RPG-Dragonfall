package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class SoulShot extends XRPGActiveSkill {
    public SoulShot(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
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
        arrow.setDamage(0);

        double damageMultiplier = 1 - getSkillVariables().getDouble(getSkillLevel(), "percent-health-damage", 0.25);

        getPlugin().projectiles.put(arrow.getUniqueId(), new ProjectileData(arrow, getXRPGPlayer().getLevel(), damageMultiplier, 20));
        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
