package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.BloodCorruptionTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;

public class BloodCorruption extends XRPGActiveSkill {

    public BloodCorruption(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doBloodCorruption(e.getPlayer());

    }

    @Override
    public void initialize() {

    }

    private void doBloodCorruption(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        double range = getSkillVariables().getDouble(getSkillLevel(), "range", 16.0);

        RayTraceResult result = Utils.rayTrace(caster, range, FluidCollisionMode.NEVER);
        if (result.getHitEntity() != null) {
            double duration = getSkillVariables().getDouble(getSkillLevel(), "duration", 4.0);
            double damage = getSkillVariables().getDouble(getSkillLevel(), "damage-per-block", 1.0);

            caster.sendMessage("Hit " + result.getHitEntity().getName());
            LivingEntity target = (LivingEntity) result.getHitEntity();
            new BloodCorruptionTask(caster, target, damage).runTaskLater(getPlugin(), (long) duration * 20L);
            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }

}
