package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.Rage;
import org.bukkit.scheduler.BukkitRunnable;

public class RavagerRageTask extends BukkitRunnable {
    private final Rage rageSkill;
    private final XRPGPlayer xrpgPlayer;
    private final byte count;

    public RavagerRageTask(XRPGPlayer xrpgPlayer, Rage rageSkill, byte count) {
        this.xrpgPlayer = xrpgPlayer;
        this.rageSkill = rageSkill;
        this.count = count;
    }

    @Override
    public void run() {
        rageSkill.decreaseCurrentRage(count);
        
        xrpgPlayer.sendActionBarMessage();
        if (rageSkill.getCurrentRage() <= 0) {
            this.cancel();
        }
    }
}
