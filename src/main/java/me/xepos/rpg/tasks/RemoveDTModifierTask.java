package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.DamageTakenSource;
import me.xepos.rpg.events.XRPGDamageTakenRemovedEvent;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveDTModifierTask extends BukkitRunnable {

    private final Player sourcePlayer;
    private final XRPGPlayer targetPlayer;
    private final DamageTakenSource source;

    public RemoveDTModifierTask(Player sourcePlayer, XRPGPlayer target, DamageTakenSource sourceAbility) {
        this.sourcePlayer = sourcePlayer;
        this.targetPlayer = target;
        this.source = sourceAbility;
    }

    @Override
    public void run() {
        XRPGDamageTakenRemovedEvent eventRemove = new XRPGDamageTakenRemovedEvent(sourcePlayer, targetPlayer.getPlayer(), source);
        Bukkit.getServer().getPluginManager().callEvent(eventRemove);
        Utils.removeDTModifier(targetPlayer, source);
    }
}
