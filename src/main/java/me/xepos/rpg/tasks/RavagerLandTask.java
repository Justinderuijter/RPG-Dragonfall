package me.xepos.rpg.tasks;

import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RavagerLandTask extends BukkitRunnable {

    private final Player player;
    private final double damage;
    private final ProtectionSet protectionSet;
    private final PartySet partyManager;

    public RavagerLandTask(Player player, double damage, ProtectionSet protectionSet, PartySet partyManager) {
        this.player = player;
        this.damage = damage;
        this.protectionSet = protectionSet;
        this.partyManager = partyManager;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }

        if (!player.isFlying())
        {
            Block block = player.getLocation().subtract(0, 0.1, 0).getBlock().getRelative(2, 0, -2);
            if(block.getType().isSolid()) {
                World world = player.getWorld();
                for (int x = 0; x < 5; x++) {
                    for (int z = 0; z < 5; z++) {
                        world.playEffect(block.getRelative(x * -1, 0, z).getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                    }
                }

                List<Entity> entities = new ArrayList<>(player.getLocation().getWorld().getNearbyEntities(player.getLocation(), 4, 2, 4, p -> p instanceof LivingEntity && p != player));
                for (Entity entity : entities) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity instanceof Player) {
                        if (protectionSet.isLocationValid(player.getLocation(), livingEntity.getLocation()) && !partyManager.isPlayerAllied(player, (Player) livingEntity)) {
                            damageAndSlowTarget(livingEntity);
                        }
                    } else {
                        damageAndSlowTarget(livingEntity);
                    }
                }
                this.cancel();
            }else if (block.getType() == Material.WATER || block.getType() == Material.LAVA){
                this.cancel();
            }
        }
    }

    private void damageAndSlowTarget(LivingEntity livingEntity) {
        livingEntity.damage(damage, player);
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, false, false, true));
    }
}
