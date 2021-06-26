package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class ExplosiveShot extends XRPGActiveSkill {
    public ExplosiveShot(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

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
        final float yield = (float) getSkillVariables().getDouble(getSkillLevel(), "explosion-yield", 2.0);
        final boolean setFire = getSkillVariables().getBoolean(getSkillLevel(), "explosion-fire", false);
        final boolean breakBlocks = getSkillVariables().getBoolean(getSkillLevel(), "explosion-break-block", false);

        ExplosiveProjectileData data = new ExplosiveProjectileData(arrow, yield, 20);
        data.setsFire(setFire);
        data.destroysBlocks(breakBlocks);

        getPlugin().projectiles.put(arrow.getUniqueId(), data);
        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
