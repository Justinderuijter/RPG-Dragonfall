package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.BleedTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ShadowSneak extends XRPGActiveSkill {

    public ShadowSneak(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
        doShadowSneak(e.getPlayer());

    }

    @Override
    public void initialize() {

    }

    private void doShadowSneak(Player player) {
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0.3, p -> p instanceof LivingEntity && p != player);
        if (result != null && result.getHitEntity() != null) {
            LivingEntity livingEntity = (LivingEntity) result.getHitEntity();

            final double batDespawnDelay = getSkillVariables().getDouble(getSkillLevel(), "despawn-delay", 3.0);
            final byte maxProcs = (byte) getSkillVariables().getInt(getSkillLevel(), "max-procs", 3);
            final double interval = getSkillVariables().getDouble(getSkillLevel(), "interval", 1.0);

            List<Bat> bats = summonBats(player);
            removeBats(bats, getPlugin(), (long) batDespawnDelay * 20);

            Vector direction = livingEntity.getLocation().getDirection().setY(0.).normalize().multiply(-2.);
            player.teleport(livingEntity.getLocation().add(direction), PlayerTeleportEvent.TeleportCause.PLUGIN);
            if (livingEntity instanceof Player && getProtectionSet().isLocationValid(player.getLocation(), livingEntity.getLocation()) && !getPartySet().isPlayerAllied(player, (Player) livingEntity)) {
                livingEntity.damage(getDamage(), player);

                new BleedTask(livingEntity, player, maxProcs, getDamage()).runTaskTimer(getPlugin(), 11, (long) interval * 20L);
            }
            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }

    private List<Bat> summonBats(Player player) {
        List<Bat> bats = new ArrayList<>();
        Vector velocity = new Vector(0, 1, 0);
        for (int i = 0; i < 8; i++) {
            Bat bat = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
            bat.setInvulnerable(true);
            bat.setCollidable(false);
            bat.setVelocity(velocity);

            bats.add(bat);
        }
        return bats;
    }

    private void removeBats(List<Bat> batList, XRPG plugin, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Bat bat : batList) {
                    bat.remove();
                }
            }
        }.runTaskLater(plugin, delay);
    }
}
