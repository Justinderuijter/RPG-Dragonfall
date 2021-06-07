package me.xepos.rpg.dependencies.combat.parties;

import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;

public class McMMOPartyManager implements IPartyManager{
    protected McMMOPartyManager(){ }

    @Override
    public boolean isPlayerAllied(Player source, Player target) {
        return UserManager.getPlayer(source).getParty().hasMember(target.getUniqueId());
    }
}
