package me.xepos.rpg.listeners;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;

public class McMMOListener implements Listener {
    private final XRPG plugin;
    private final HashSet<PrimarySkillType> blacklistedSkills;

    public McMMOListener(XRPG plugin){
        this.plugin = plugin;
        this.blacklistedSkills = new HashSet<>();

        for (String skillString:plugin.getConfig().getStringList("exp.source-blacklist.mcmmo-skills")) {
            try{
                blacklistedSkills.add(PrimarySkillType.valueOf(skillString.toUpperCase()));
            }catch (IllegalArgumentException ex){
                Bukkit.getLogger().warning(skillString + " is not a valid McMMO skill! Skipping...");
            }
        }
    }

    @EventHandler
    public void onMcMMOEXPGain(McMMOPlayerXpGainEvent e){
        XRPGPlayer gainer = plugin.getXRPGPlayer(e.getPlayer());

        if (gainer != null && gainer.canGainEXP() && !blacklistedSkills.contains(e.getSkill())){
            gainer.addExp(e.getRawXpGained() * plugin.getConfig().getDouble("exp.global-multiplier", 1.0));
        }
    }
}
