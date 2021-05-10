package me.xepos.rpg.tasks;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ApplyStunTask extends BukkitRunnable {

    private final XRPGPlayer target;
    private final AttributeModifier modifier;
    private final long duration;
    private final XRPG plugin;

    public ApplyStunTask(XRPGPlayer target, AttributeModifier modifier, long duration, XRPG plugin) {
        this.target = target;
        this.modifier = modifier;
        this.duration = duration;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Player player = target.getPlayer();
        target.setStunned(true);
        Utils.addUniqueModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, modifier);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1F, 1F);

        new RemoveStunTask(target, modifier).runTaskLater(plugin, duration);

    }
}
