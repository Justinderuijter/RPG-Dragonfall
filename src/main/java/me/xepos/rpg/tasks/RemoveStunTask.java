package me.xepos.rpg.tasks;

import me.xepos.rpg.utils.Utils;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveStunTask extends BukkitRunnable {

    private XRPGPlayer stunnedPlayer;
    private AttributeModifier modifier;

    public RemoveStunTask(XRPGPlayer stunnedPlayer, AttributeModifier modifier)
    {
        this.stunnedPlayer = stunnedPlayer;
        this.modifier = modifier;
    }

    @Override
    public void run() {
        if (stunnedPlayer.getPlayer() != null && stunnedPlayer.getPlayer().isOnline()) {

            Utils.removeUniqueModifier(stunnedPlayer.getPlayer(), Attribute.GENERIC_MOVEMENT_SPEED, modifier);
            if (stunnedPlayer.isStunned())
                stunnedPlayer.setStunned(false);

        }
    }
}
