package me.xepos.rpg.events;

import me.xepos.rpg.enums.DTRemoveReason;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.entity.Player;

public class XRPGDamageTakenRemovedEvent extends XRPGDamageTakenModifiedEvent {
    private final DTRemoveReason removeReason;

    public XRPGDamageTakenRemovedEvent(Player sourcePlayer, Player targetPlayer, XRPGSkill source, DTRemoveReason removeReason) {
        super(sourcePlayer, targetPlayer, source);

        this.removeReason = removeReason;
    }

    public XRPGDamageTakenRemovedEvent(Player sourcePlayer, Player targetPlayer, XRPGSkill source) {
        super(sourcePlayer, targetPlayer, source);

        this.removeReason = DTRemoveReason.NATURAL;
    }

    public DTRemoveReason getRemoveReason() {
        return removeReason;
    }
}
