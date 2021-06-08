package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class ShortFuse extends XRPGSkill {
    public ShortFuse(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
        Player player = e.getPlayer();


        if (!isSkillReady()){
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }
        final float baseYield = (float)getSkillVariables().getDouble("explosion-base-yield", 3.0) - 1;
        final boolean setFire = getSkillVariables().getBoolean("explosion-set-fire", false);
        final boolean breakBlocks = getSkillVariables().getBoolean("explosion-break-blocks", false);

        setRemainingCooldown(getCooldown());

        Location location = player.getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_CREEPER_PRIMED, 1F, 1F);
        location.getWorld().playEffect(location, Effect.SMOKE, 1);

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            player.getWorld().createExplosion(player.getLocation(), calculateExplosionYield(baseYield, getSkillLevel()), setFire, breakBlocks, player);
        }, calculateFuse(getSkillVariables().getDouble("base-fuse-time", 1.5), getSkillLevel()));

    }

    @Override
    public void initialize() {

    }

    private float calculateExplosionYield(float baseYield, int level){
        final int yieldPerLevel = getSkillVariables().getInt("yield-per-level");

        int power = 0;
        for (int i = 1; i < level; i++) {
            if (level % 3 == 0) continue;
            power += yieldPerLevel;
        }

        return baseYield + power;
    }

    private long calculateFuse(double baseCooldown, int level){
        //Results multiplied by 20 to convert to ticks

        if (level % 3 == 0){
            final int reductionLevel = level / 3;

            double cooldown = baseCooldown;
            for (int i = 0; i < reductionLevel; i++) {
                cooldown = cooldown * 0.666;
            }

            return (long)cooldown * 20;
        }
        return (long)baseCooldown * 20;
    }
}
