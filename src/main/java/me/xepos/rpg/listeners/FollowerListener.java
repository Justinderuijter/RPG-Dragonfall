package me.xepos.rpg.listeners;

import me.xepos.rpg.XRPG;
import org.bukkit.event.Listener;

public class FollowerListener implements Listener {

    private final XRPG plugin;

    public FollowerListener(XRPG plugin) {
        this.plugin = plugin;
    }

/*    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        doNecromancerCheck(e);
    }

    public void doNecromancerCheck(EntityDeathEvent e) {
        if (((CraftEntity) e.getEntity()).getHandle() instanceof Follower) {
            Follower follower = (Follower) ((CraftLivingEntity) e.getEntity()).getHandle();
            if (follower.getOwner() instanceof Player) {
                XRPGPlayer xrpgPlayer = plugin.getXRPGPlayer((Player) follower.getOwner(), true);

                if (xrpgPlayer != null) {
                    for (IFollowerContainer skill : xrpgPlayer.getFollowerSkills()) {
                        skill.getFollowers().remove(follower);
                    }
                }
            }

        }
    }*/
}
