package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ShowPlayerTask extends BukkitRunnable {

    private Player targetPlayer;
    private final List<Player> playersToShow = new ArrayList<>();
    private final XRPG plugin;
    private boolean queryAllOnlinePlayers = false;

    public ShowPlayerTask(XRPG plugin, Player targetPlayer) {
        this.targetPlayer = targetPlayer;
        this.plugin = plugin;
        this.queryAllOnlinePlayers = true;
    }

    public ShowPlayerTask(XRPG plugin, Player targetPlayer, List<Player> playersToShow) {
        this.targetPlayer = targetPlayer;
        this.playersToShow.addAll(playersToShow);
        this.plugin = plugin;
    }

    public ShowPlayerTask(XRPG plugin, Player targetPlayer, Player playerToShow) {
        this.targetPlayer = targetPlayer;
        this.playersToShow.add(playerToShow);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (queryAllOnlinePlayers) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                targetPlayer.showPlayer(plugin, player);
            }
        } else {
            for (Player player : playersToShow) {
                targetPlayer.showPlayer(plugin, player);
            }
        }

    }
}
