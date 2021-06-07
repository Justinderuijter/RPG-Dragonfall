package me.xepos.rpg.dependencies.combat.parties;

import me.xepos.rpg.dependencies.combat.pvptoggle.IPvPToggle;
import org.bukkit.entity.Player;

import java.util.Set;

public class PartySet {
    private final IPvPToggle pvpToggle;
    private final Set<IPartyManager> partyManagers;

    public PartySet(IPvPToggle pvpToggle, Set<IPartyManager> partyManagers){
        this.pvpToggle = pvpToggle;
        this.partyManagers = partyManagers;
    }

    public boolean canHurtPlayer(Player source, Player caster){
        if (!pvpToggle.hasPvPEnabled(caster)) return false;

        for (IPartyManager manager:partyManagers) {
            if (!manager.isPlayerAllied(source, caster)) return false;
        }

        return true;
    }

    public boolean isPlayerAllied(Player source, Player caster){
        for (IPartyManager manager:partyManagers) {
            if (manager.isPlayerAllied(source, caster)) return true;
        }
        return false;
    }
}
