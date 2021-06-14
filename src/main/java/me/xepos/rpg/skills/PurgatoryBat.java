package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.PurgatoryBatTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;

public class PurgatoryBat extends XRPGActiveSkill {

    public PurgatoryBat(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doPurgatoryBat(e.getPlayer());
    }

    @Override
    public void initialize() {

    }

    private void doPurgatoryBat(Player player) {
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double range = getSkillVariables().getDouble("range", 16.0);
        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), range, FluidCollisionMode.ALWAYS, true, 0.3, p -> p instanceof LivingEntity && p != player);

        if (result != null && result.getHitEntity() != null) {
            LivingEntity livingEntity = (LivingEntity) result.getHitEntity();

            final double interval = getSkillVariables().getDouble("interval", 1.0);
            final byte maxCount = (byte) getSkillVariables().getInt("max-procs", 5);
            final double duration = getSkillVariables().getDouble("dt-duration", 5);
            final double dtAmount = getSkillVariables().getDouble("dt-amount", 1.2);

            Bat bat = (Bat) livingEntity.getWorld().spawnEntity(livingEntity.getEyeLocation(), EntityType.BAT);
            bat.setAI(false);
            bat.setInvulnerable(true);
            bat.setCollidable(false);
            bat.setAwake(true);
            bat.setCustomName("Purgatory bat");
            bat.setCustomNameVisible(false);

            new PurgatoryBatTask(bat, player, getDamage(), maxCount, this, dtAmount, getPlugin(), (long) duration * 20L)
                    .runTaskTimer(getPlugin(), 10, (long) interval * 20L);

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }
}
