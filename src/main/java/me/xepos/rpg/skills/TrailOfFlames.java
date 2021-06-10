package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

        setRemainingCooldown(getCooldown());

        final double duration = getSkillVariables().getDouble("duration", 5.0);
        final long delay = 2;
        AtomicInteger ticks = new AtomicInteger();

        new BukkitRunnable() {
            final Set<Location> locations = new HashSet<>();
            final boolean ignoreVillagers = getSkillVariables().getBoolean("ignore-villagers", true);

            @Override
            public void run() {
                if (ticks.get() >= duration * 20){
                    for (Location location:locations) {
                        if (location.getBlock().getType() == Material.AIR){
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendBlockChange(location, Material.AIR.createBlockData());
                            }
                        }
                    }
                    cancel();
                    return;
                }

                if (caster.getLocation().getBlock().getType() == Material.AIR) {
                    if (caster.getLocation().clone().subtract(0, 0.5, 0).getBlock().getType() != Material.AIR) {
                        locations.add(caster.getLocation());
                        for (Location location:locations) {
                            Set<Entity> entities = new HashSet<>(location.getWorld().getNearbyEntities(location, 1, 1, 1, p -> p instanceof LivingEntity));
                            for (Entity entity:entities) {
                                if (entity instanceof Villager && ignoreVillagers) continue;
                                else if (entity instanceof Player && canHurtTarget(((Player)entity))) continue;
                                if (entity.getFireTicks() < 200){
                                    entity.setFireTicks(200);
                                }
                            }
                        }
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendBlockChange(caster.getLocation(), Material.SOUL_FIRE.createBlockData());
                        }
                    }
                }
                ticks.set(ticks.get() + (int)delay);
            }
        }.runTaskTimer(getPlugin(), delay, delay);

    }

}
