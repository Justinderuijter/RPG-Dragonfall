package me.xepos.rpg.utils;

import java.util.ArrayList;
import java.util.List;

public class DependencyUtils {

    @SuppressWarnings("unused")
    public static void checkFactions3Files() throws ClassNotFoundException, LinkageError
    {
            Class<?> checkForMPlayer = Class.forName("com.massivecraft.factions.entity.MPlayer");
            Class<?> checkForFaction = Class.forName("com.massivecraft.factions.entity.Faction");
            Class<?> checkForRel = Class.forName("com.massivecraft.factions.Rel");
    }

    @SuppressWarnings("unused")
    public static void checkFactionsUUIDFiles() throws ClassNotFoundException, LinkageError
    {
        Class<?> checkForPlayer = Class.forName("com.massivecraft.factions.FPlayer");
        Class<?> checkForPlayers = Class.forName("com.massivecraft.factions.FPlayers");
    }

    @SuppressWarnings("unused")
    public static void checkWorldEditFiles() throws ClassNotFoundException, LinkageError
    {
        List<String> classes = new ArrayList<>();
        classes.add("com.sk89q.worldedit.bukkit.BukkitAdapter");
        classes.add("com.sk89q.worldedit.bukkit.BukkitPlayer");
        classes.add("com.sk89q.worldguard.WorldGuard");
        classes.add("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
        classes.add("com.sk89q.worldguard.protection.flags.registry.FlagConflictException");
        classes.add("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
        classes.add("com.sk89q.worldguard.protection.regions.RegionContainer");
        classes.add("com.sk89q.worldguard.protection.regions.RegionQuery");

        for (String classString: classes) {
            Class<?> classToCheck = Class.forName(classString);
        }
    }
}
