package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class TrailOfFlames extends XRPGActiveSkill {
    private final static Set<Material> allowedBlocks = new HashSet<Material>(){{
        add(Material.AIR);
        add(Material.GRASS);
        add(Material.TALL_GRASS);
    }};

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
            @Override
            public void run() {
                if (ticks.get() >= duration * 20){
                    cancel();
                    return;
                }

                if (allowedBlocks.contains(caster.getLocation().getBlock().getType())){
                    caster.getLocation().getBlock().setType(Material.SOUL_FIRE);
                }
                ticks.set(ticks.get() + (int)delay);
            }
        }.runTaskTimer(getPlugin(), delay, delay);

    }

}
