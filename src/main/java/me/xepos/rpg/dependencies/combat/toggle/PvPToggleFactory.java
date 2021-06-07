package me.xepos.rpg.dependencies.combat.toggle;

import me.xepos.rpg.XRPG;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPToggleFactory {
    private final static boolean usePvPToggle = JavaPlugin.getPlugin(XRPG.class).getConfig().getBoolean("use-pvp-toggle", false);

    public static IPvPToggle getPvPToggle(boolean pvpTogglePluginActive){
        if (usePvPToggle && pvpTogglePluginActive){
            return new PvPToggleHook();
        }
        return new DefaultPvPToggle();
    }
}
