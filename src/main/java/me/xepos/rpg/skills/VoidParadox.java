package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.ShowPlayerTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;

public class VoidParadox extends XRPGActiveSkill {

    public VoidParadox(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doVoidParadox(e.getPlayer());

    }

    @Override
    public void initialize() {

    }

    private void doVoidParadox(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        double range = getSkillVariables().getDouble(getSkillLevel(),"range", 16);

        RayTraceResult result = Utils.rayTrace(caster, range, FluidCollisionMode.NEVER);
        if (result.getHitEntity() != null) {
            LivingEntity target = (LivingEntity) result.getHitEntity();
            if (target instanceof Player) {
                Player targetPlayer = (Player) target;
                if (!getPartySet().isPlayerAllied(caster, targetPlayer) && getProtectionSet().isLocationValid(caster.getLocation(), targetPlayer.getLocation())) {

                    final double duration = getSkillVariables().getDouble(getSkillLevel(), "duration", 5.0);

                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        targetPlayer.hidePlayer(getPlugin(), player);
                    }
                    new ShowPlayerTask(getPlugin(), targetPlayer).runTaskLater(getPlugin(), (long) duration * 20L);
                }
            } else {
                target.damage(getDamage(), caster);
            }

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }
}
