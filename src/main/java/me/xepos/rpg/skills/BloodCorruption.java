package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tasks.BloodCorruptionTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class BloodCorruption extends XRPGSkill {

    public BloodCorruption(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("LEFT_CLICK").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Blood")) {
            doBloodCorruption(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doBloodCorruption(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        double range = getSkillVariables().getDouble("range", 16.0);

        RayTraceResult result = Utils.rayTrace(caster, range, FluidCollisionMode.NEVER);
        if (result.getHitEntity() != null) {
            double duration = getSkillVariables().getDouble("duration", 4.0);
            double damage = getSkillVariables().getDouble("damage-per-block", 1.0);

            caster.sendMessage("Hit " + result.getHitEntity().getName());
            LivingEntity target = (LivingEntity) result.getHitEntity();
            new BloodCorruptionTask(caster, target, damage).runTaskLater(getPlugin(), (long) duration * 20L);
            setRemainingCooldown(getCooldown());
        }
    }

}
