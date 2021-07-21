package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.Flamethrower;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class FlamethrowerTask extends BukkitRunnable {
    private final XRPGPlayer xrpgPlayer;
    private final Flamethrower flamethrowerSkill;
    private final Player player;
    private final double damage;
    private final long interval;
    private final double inaccuracy;
    private final Random random;
    private final XRPG plugin = XRPG.getInstance();

    private int count = 0;

    public FlamethrowerTask(XRPGPlayer caster, Flamethrower flamethrowerSkill, double damage, double inaccuracy, long interval){
        this.damage = damage;
        this.flamethrowerSkill = flamethrowerSkill;
        this.xrpgPlayer = caster;
        this.interval = interval;
        this.inaccuracy = inaccuracy;
        this.player = caster.getPlayer();
        this.random = new Random();
    }

    @Override
    public void run() {
        if (player != null && player.isValid()){
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 1F);
            Vector v = player.getLocation().getDirection();
            v.add(new Vector(random.nextDouble() * inaccuracy - (inaccuracy / 2), random.nextDouble() * inaccuracy - (inaccuracy / 2), random.nextDouble() * inaccuracy - (inaccuracy / 2)));
            SmallFireball fireball = player.launchProjectile(SmallFireball.class);
            fireball.setDirection(v);

            ProjectileData projectileData = new ProjectileData(fireball, xrpgPlayer.getLevel(), damage, 20);
            projectileData.setFireTicks(60);

            plugin.projectiles.put(fireball.getUniqueId(), projectileData);

            count++;

            if (count * interval >= 20){
                count = 0;
                if (flamethrowerSkill.hasRequiredMana()){
                    flamethrowerSkill.updatedCasterMana();
                }else{
                    this.cancel();
                }
            }
        }else{
            this.cancel();
        }
    }


}
