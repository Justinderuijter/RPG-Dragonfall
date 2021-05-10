package me.xepos.rpg.database;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.ClassLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseManagerFactory {

    public static IDatabaseManager getDatabaseManager(ClassLoader classLoader) {

        return JavaPlugin.getPlugin(XRPG.class).getConfig().getBoolean("MySQL.use MySQL", false) ? new MySQLDatabaseManager(classLoader) : new JSONDatabaseManager(classLoader);
    }
}
