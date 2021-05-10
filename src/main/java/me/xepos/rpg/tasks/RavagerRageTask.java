package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.Rage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;

//This task should be ran async considering it doesn't call Bukkit API
public class RavagerRageTask extends BukkitRunnable {
    private Rage rageSkill;
    private XRPGPlayer xrpgPlayer;
    private byte count;

    public RavagerRageTask(XRPGPlayer xrpgPlayer, Rage rageSkill, byte count) {
        this.xrpgPlayer = xrpgPlayer;
        this.rageSkill = rageSkill;
        this.count = count;
    }

    @Override
    public void run() {
        rageSkill.decreaseCurrentRage(count);
        xrpgPlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Current Rage: " + rageSkill.getCurrentRage() + " (-)", ChatColor.RED));
        if (rageSkill.getCurrentRage() <= 0) {
            this.cancel();
        }
    }
}
