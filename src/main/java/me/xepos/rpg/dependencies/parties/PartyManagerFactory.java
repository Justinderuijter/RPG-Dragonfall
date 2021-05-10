package me.xepos.rpg.dependencies.parties;


import me.xepos.rpg.utils.DependencyUtils;
import me.xepos.rpg.XRPG;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getServer;

public class PartyManagerFactory {

    private final static String configPartyManager = JavaPlugin.getPlugin(XRPG.class).getConfig().getString("Party Manager Type", "Factions");

    public static IPartyManager getPartyManager() {

        if (configPartyManager != null) {
            Plugin plugin = getServer().getPluginManager().getPlugin(configPartyManager);
            if (plugin != null && plugin.isEnabled()) {
                if (configPartyManager.equalsIgnoreCase("Parties"))
                    return new PartiesManager();

                else if (configPartyManager.equalsIgnoreCase("factions"))
                    return checkFactions();
            }
        }
        return new DefaultManager();
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
