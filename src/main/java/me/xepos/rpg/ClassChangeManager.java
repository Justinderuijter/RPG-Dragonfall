package me.xepos.rpg;

import me.xepos.rpg.configuration.SkillLoader;
import me.xepos.rpg.database.DatabaseManager;
import me.xepos.rpg.datatypes.ClassData;
import me.xepos.rpg.datatypes.ClassInfo;
import me.xepos.rpg.datatypes.PlayerData;
import me.xepos.rpg.events.classes.XRPGClassChangeEvent;
import me.xepos.rpg.tasks.SetHealthCallable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ClassChangeManager {
    private final XRPG plugin;
    private final DatabaseManager databaseManager;
    private final SkillLoader skillLoader;
    private Material material;

    public ClassChangeManager(XRPG plugin, DatabaseManager databaseManager, SkillLoader skillLoader) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.skillLoader = skillLoader;

        String materialString = plugin.getConfig().getString("class-change.material");
        try {
            this.material = Material.valueOf(materialString);
        } catch (IllegalArgumentException ex) {
            this.material = Material.GOLD_INGOT;
            Bukkit.getLogger().severe("Could not find material: " + materialString + "!");
            Bukkit.getLogger().severe("Using default: GOLD_INGOT.");
        }
    }

    public boolean changeClass(XRPGPlayer xrpgPlayer, String newClassId, boolean isFree) {
        if (xrpgPlayer.getPlayer() == null || newClassId == null) return false;

        if (xrpgPlayer.getClassId().equalsIgnoreCase(newClassId)) {
            final TextComponent textComponent = Component.text(ChatColor.RED + "You already are " + xrpgPlayer.getClassDisplayName() + "!");
            xrpgPlayer.getPlayer().sendMessage(textComponent);
            return false;
        }

        ClassInfo classInfo = plugin.getClassInfo(newClassId);
        if (classInfo == null){
            final TextComponent error = Component.text(ChatColor.RED + "Something went wrong while changing your class!");
            final TextComponent advice = Component.text(ChatColor.RED + "Please notify your server administrator!");
            xrpgPlayer.getPlayer().sendMessage(error);
            xrpgPlayer.getPlayer().sendMessage(advice);
            Bukkit.getLogger().severe("Could not find class info for " + newClassId);
            return false;
        }

        if (!isFree) {
            final int paidSwapLevel = plugin.getConfig().getInt("class-change.costs-after-level", 9);
            if (paidSwapLevel != -1 && xrpgPlayer.getLevel() > paidSwapLevel) {
                final int amount = plugin.getConfig().getInt("class-change.amount", 32);
                if (!xrpgPlayer.getPlayer().getInventory().containsAtLeast(new ItemStack(material), amount)) {
                    xrpgPlayer.getPlayer().sendMessage(ChatColor.RED + "You do not have enough gold ingots to change your class!");
                    return false;
                }
                xrpgPlayer.getPlayer().getInventory().removeItem(new ItemStack(material, amount));
            }
        }

        XRPGClassChangeEvent event = new XRPGClassChangeEvent(xrpgPlayer.getPlayer(), xrpgPlayer.getClassId(), xrpgPlayer.getClassDisplayName(), newClassId, classInfo.getDisplayName());
        Bukkit.getServer().getPluginManager().callEvent(event);

        //PlayerData data = xrpgPlayer.extractData();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerData data = databaseManager.savePlayerData(xrpgPlayer);

            data.setClassId(event.getNewClassId());

            Component text;
            if (StringUtils.isBlank(xrpgPlayer.getClassId())){
                text = xrpgPlayer.getPlayer().displayName().append(Component.text(" selected " + classInfo.getDisplayName() + " as their first class!"));
            }else{
                text = xrpgPlayer.getPlayer().displayName().append(Component.text(" changed their class from " + xrpgPlayer.getClassDisplayName() + " to " + classInfo.getDisplayName() + "!"));
            }
            Bukkit.broadcast(text);

            skillLoader.loadPlayerSkills(data, xrpgPlayer);

            ClassData classData = data.getClassData(data.getClassId());
            if (classData != null){
                final double health = 20;
                double lastHealth = classData.getLastHealth();
                if (lastHealth == 0) lastHealth = health;

                Bukkit.getScheduler().callSyncMethod(plugin, new SetHealthCallable(xrpgPlayer.getPlayer(), lastHealth));
            }
        });

        xrpgPlayer.getPlayer().closeInventory();
        return true;
    }
}
