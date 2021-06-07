package me.xepos.rpg.dependencies.combat.parties;


import me.xepos.rpg.XRPG;
import me.xepos.rpg.utils.DependencyUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PartyManagerFactory {
    private final static List<String> configPartyManagers = JavaPlugin.getPlugin(XRPG.class).getConfig().getStringList("party-managers");

    public static Set<IPartyManager> getPartyManager() {
        final Set<IPartyManager> managers = new HashSet<>();
        for (String managerName:configPartyManagers) {
            if (Bukkit.getPluginManager().getPlugin(managerName) != null){
                switch (managerName.toLowerCase()){
                    case "factions":
                        managers.add(checkFactions());
                        break;
                    case "mcmmo":
                        managers.add(new McMMOPartyManager());
                        break;
                    case "parties":
                        managers.add(new PartiesManager());
                        break;
                    case "towny":
                        managers.add(new TownyAdvancedManager());
                        break;
                }
            }
        }
        if (managers.isEmpty()) managers.add(new DefaultManager());

        return managers;
    }

    @SuppressWarnings("unused")
    private static IPartyManager checkFactions() {
        boolean isFactionsUUID = true;
        boolean isFactions3 = true;
        IPartyManager manager = new DefaultManager();
        try {
            DependencyUtils.checkFactionsUUIDFiles();
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            isFactionsUUID = false;
        }

        try {
            DependencyUtils.checkFactions3Files();
        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            isFactions3 = false;
        }

        if (isFactions3 && !isFactionsUUID) {
            System.out.println("Found Factions3");
            return new FactionsManager();
        } else if (isFactionsUUID && !isFactions3) {
            System.out.println("Found FactionsUUID");
            return new FactionsUUIDManager();
        } else {
            return new DefaultManager();
        }
    }
}
