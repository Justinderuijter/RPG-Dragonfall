package me.xepos.rpg.dependencies.combat.protection;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.utils.DependencyUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class ProtectionSetFactory {
    private final static List<String> configManagers = JavaPlugin.getPlugin(XRPG.class).getConfig().getStringList("protection-managers");
    private final static List<IProtectionManager> managerList = new ArrayList<>();

    public static ProtectionSet getProtectionRules() {
        boolean isFactions3 = false;
        boolean isFactionsUUID = false;
        boolean isWorldGuard7 = false;

        for (String managerString : configManagers) {
            Plugin plugin = getServer().getPluginManager().getPlugin(managerString);
            if (plugin != null && plugin.isEnabled()) {
                if (managerString.equalsIgnoreCase("worldguard")) {

                    try {
                        DependencyUtils.checkWorldEditFiles();
                        isWorldGuard7 = true;
                    } catch (Exception ex) {
                        System.out.println("Skipping WorldGuard, is it outdated?");
                    }

                    if (isWorldGuard7) {
                        managerList.add(new WorldGuardProtectionManager());
                    }

                } else if (managerString.equalsIgnoreCase("factions")) {
                    try {
                        DependencyUtils.checkFactions3Files();
                        isFactions3 = true;
                    } catch (Exception ignored) {
                        //Only used to verify which fork of factions this is
                    }

                    try {
                        DependencyUtils.checkFactionsUUIDFiles();
                        isFactionsUUID = true;
                    } catch (Exception ignored) {
                        //Only used to verify which fork of factions this is
                    }

                    if (isFactions3 && !isFactionsUUID) {
                        System.out.println("Detected Factions3");
                        managerList.add(new FactionsProtectionManager());
                    }
                    else if (!isFactions3 && isFactionsUUID) {
                        System.out.println("Detected FactionsUUID");
                        managerList.add(new FactionsUUIDProtectionManager());
                    }

                }
            }
        }

        if (managerList.size() == 0)
            managerList.add(new DefaultProtectionManager());

        return new ProtectionSet(managerList);
    }
}
