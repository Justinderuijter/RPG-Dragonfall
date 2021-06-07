package me.xepos.rpg.dependencies.parties;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

public class TownyAdvancedManager implements IPartyManager{
    private final TownyUniverse towny = TownyUniverse.getInstance();

    protected TownyAdvancedManager(){ }

    @Override
    public boolean isPlayerAllied(Player source, Player target) {
        final Resident sourceResident = towny.getResident(source.getUniqueId());
        final Resident targetResident = towny.getResident(target.getUniqueId());

        if (sourceResident == null || targetResident == null) return false;

        try{
            final Town sourceTown = sourceResident.getTown();
            if (sourceTown.hasResident(targetResident)){
                return true;
            }else{
                final Town targetTown = targetResident.getTown();
                return sourceTown.isAlliedWith(targetTown);
            }
        }catch (NotRegisteredException ex){
            return false;
        }
    }
}