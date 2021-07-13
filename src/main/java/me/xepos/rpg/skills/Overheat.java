package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.OverheatTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;

public class Overheat extends XRPGActiveSkill {

    public Overheat(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        doOverheat(e.getPlayer());

    }

    @Override
    public void initialize() {

    }

    private void doOverheat(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        double range = getSkillVariables().getDouble(getSkillLevel(), "range", 16.0);

        RayTraceResult result = Utils.rayTrace(caster, range, FluidCollisionMode.NEVER);
        if (result != null && result.getHitEntity() != null) {

            LivingEntity target = (LivingEntity) result.getHitEntity();
            double delay = getSkillVariables().getDouble(getSkillLevel(), "delay", 5.0);

            result.getHitEntity().setVisualFire(true);
            //Utils.rayTrace only returns livingEntities so no need to check
            target.sendMessage(ChatColor.RED + "You've been hit by Overheat!");
            target.sendMessage(ChatColor.RED + "Get in water to reduce the damage!");

            new OverheatTask(target, getDamage(), getSkillVariables().getDouble(getSkillLevel(),"damage-per-armor", 0.5)).runTaskLater(getPlugin(), (long) delay * 20L);
            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }

}
