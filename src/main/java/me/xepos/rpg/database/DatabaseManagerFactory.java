package me.xepos.rpg.database;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.configuration.SkillLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseManagerFactory {

    public static IDatabaseManager getDatabaseManager(SkillLoader skillLoader) {

        return JavaPlugin.getPlugin(XRPG.class).getConfig().getBoolean("MySQL.use-MySQL", false) ? new MySQLDatabaseManager(skillLoader) : new JSONDatabaseManager(skillLoader);
    }
}
