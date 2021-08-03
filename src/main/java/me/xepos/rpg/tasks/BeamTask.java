package me.xepos.rpg.tasks;

import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.skills.HealingBeam;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class BeamTask extends BukkitRunnable {
    private final Player beamCaster;
    private final HealingBeam healingBeam;
    private final double heal;
    private final double damage;
    private final double maxDistance;
    private final int interval;
    private final PartySet partySet;

    private double currentRange;
    private int count = 0;

    public BeamTask(Player beamCaster, HealingBeam healingBeam, double heal, double damage, double maxDistance, int interval, PartySet partySet){
        this.beamCaster = beamCaster;
        this.healingBeam = healingBeam;
        this.heal = heal;
        this.damage = damage;
        this.maxDistance = maxDistance;
        this.currentRange = maxDistance;
        this.interval = interval;
        this.partySet = partySet;
    }

    @Override
    public void run() {
        if(!beamCaster.isOnline() || beamCaster.isDead()){
            this.cancel();
            return;
        }
        RayTraceResult result = Utils.rayTrace(beamCaster, currentRange, FluidCollisionMode.NEVER);

        if (result == null){
            currentRange = maxDistance;
        }else if (result.getHitEntity() != null){
            LivingEntity target = (LivingEntity) result.getHitEntity();

            currentRange = target.getLocation().distance(beamCaster.getEyeLocation());
            if (target instanceof Player targetPlayer){ //Potential ally
                if (partySet.isPlayerAllied(beamCaster, targetPlayer)){
                    Utils.healLivingEntity(target, heal);
                }else if(partySet.canHurtPlayer(beamCaster, targetPlayer)){
                    targetPlayer.damage(damage, beamCaster);
                }
            }else if (target instanceof Monster || target instanceof EnderDragon){
                //Enderdragon isn't a monster somehow
                target.damage(damage, beamCaster);
            }else if (target instanceof Tameable tameable){ //Potential ally's pet
                if (tameable.getOwner() instanceof Player tameableOwner){
                    if (partySet.isPlayerAllied(beamCaster, tameableOwner)){
                        Utils.healLivingEntity(tameable, heal);
                    }else if (partySet.canHurtPlayer(beamCaster, tameableOwner)){
                        tameable.damage(damage, beamCaster);
                    }
                }
            }else{
                Utils.healLivingEntity(target, heal);
            }
        }
        Location start = beamCaster.getEyeLocation().clone();
        Vector vector = start.getDirection().normalize();
        for (int i = 0; i < Math.round(currentRange); i++) {
            start.add(vector);
            start.getWorld().spawnParticle(Particle.SCRAPE, start, 1);
        }


        count++;

        if (count * interval >= 20){
            count = 0;
            healingBeam.setRemainingCooldown(healingBeam.getCooldown());
            if (healingBeam.hasRequiredMana()){
                healingBeam.updatedCasterMana();
            }else{
                this.cancel();
            }
        }
    }
}
