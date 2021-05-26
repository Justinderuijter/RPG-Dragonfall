package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class TrailOfFlames extends XRPGActiveSkill {

    public TrailOfFlames(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doTrailOfFlames(e.getPlayer());

    }

    @Override
    public void initialize() {

    }

    private void doTrailOfFlames(Player caster) {
        if (!isSkillReady()) {
            caster.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        final Set<Location> locations = new HashSet<>();


        final boolean areVillagersIgnored = getSkillVariables().getBoolean("ignore-villagers", true);
        final double interval = getSkillVariables().getDouble("interval", 0.5);
        final byte maxProcs = (byte) getSkillVariables().getInt("max-procs", 10);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= maxProcs) {
                    cancel();
                    return;
                }
                locations.add(caster.getLocation());

                count++;
            }
        }.runTaskTimer(getPlugin(), 10, (long) interval * 20);

        new BukkitRunnable() {
            final Set<LivingEntity> livingEntities = new HashSet<>();
            int count = 0;

            @Override
            public void run() {
                if (count >= maxProcs) {
                    cancel();
                    return;
                }

                for (Location location : locations) {
                    location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 2);
                    //location.getWorld().spawnParticle(Particle.FALLING_LAVA, location.clone().add(0, 2, 0), 5, 0.5, 0, 0.5, 2);
                    location.getWorld().getNearbyEntities(location, 1, 2, 1, p -> p instanceof LivingEntity && p != caster && !livingEntities.contains(p)).forEach(x -> {
                        livingEntities.add((LivingEntity) x);
                    });

                }

                for (LivingEntity livingEntity : livingEntities) {
                    if ((livingEntity instanceof Villager && areVillagersIgnored) ||
                            (livingEntity instanceof Player && !getProtectionSet().isLocationValid(caster.getLocation(), livingEntity.getLocation()))) {
                        //Skip over villagers if the config setting is true.
                        //Skip over players if they are allied or are in safezone.
                        continue;
                    }
                    livingEntity.damage(getDamage(), caster);
                }

                count++;
            }
        }.runTaskTimer(getPlugin(), 20, (long) interval * 20);

        setRemainingCooldown(getCooldown());


    }

}
