package me.xepos.rpg.events;

import me.xepos.rpg.enums.DTRemoveReason;
import me.xepos.rpg.enums.DamageTakenSource;
import org.bukkit.entity.Player;

public class XRPGDamageTakenRemovedEvent extends XRPGDamageTakenModifiedEvent {
    private final DTRemoveReason removeReason;

    public XRPGDamageTakenRemovedEvent(Player sourcePlayer, Player targetPlayer, DamageTakenSource source, DTRemoveReason removeReason) {
        super(sourcePlayer, targetPlayer, source);

        this.removeReason = removeReason;
    }

    public XRPGDamageTakenRemovedEvent(Player sourcePlayer, Player targetPlayer, DamageTakenSource source) {
        super(sourcePlayer, targetPlayer, source);

        this.removeReason = DTRemoveReason.NATURAL;
    }

    public DTRemoveReason getRemoveReason() {
        return removeReason;
    }
}
