package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.entities.Follower;
import me.xepos.rpg.skills.base.IFollowerContainer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class FollowerListener implements Listener {

    private final XRPG plugin;

    public FollowerListener(XRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        doNecromancerCheck(e);
    }

    public void doNecromancerCheck(EntityDeathEvent e) {
        if (((CraftEntity) e.getEntity()).getHandle() instanceof Follower) {
            Follower follower = (Follower) ((CraftLivingEntity) e.getEntity()).getHandle();
            if (follower.getOwner() instanceof Player) {
                XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer((Player) follower.getOwner());

                for (IFollowerContainer skill : xrpgPlayer.getFollowerSkills()) {
                    skill.getFollowers().remove(follower);
                }
            }

        }
    }
}
