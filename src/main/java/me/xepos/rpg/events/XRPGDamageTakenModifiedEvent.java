package me.xepos.rpg.events;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class XRPGDamageTakenModifiedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final XRPGPlayer xrpgPlayer;
    private final XRPGPlayer targetPlayer;
    private final XRPGSkill skill;

    public XRPGDamageTakenModifiedEvent(Player sourcePlayer, Player targetPlayer, XRPGSkill skill) {
        super(!Bukkit.isPrimaryThread());
        final XRPG plugin = XRPG.getPlugin(XRPG.class);
        this.xrpgPlayer = plugin.getXRPGPlayer(sourcePlayer);
        this.targetPlayer = plugin.getXRPGPlayer(targetPlayer);
        this.skill = skill;

        this.xrpgPlayer.recalculateDamageTakenMultiplier();
    }


    public XRPGPlayer getSourceXRPGPlayer() {
        return xrpgPlayer;
    }

    public XRPGPlayer getTargetXRPGPlayer() {
        return targetPlayer;
    }

    public XRPGSkill getSourceSkill() {
        return skill;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
