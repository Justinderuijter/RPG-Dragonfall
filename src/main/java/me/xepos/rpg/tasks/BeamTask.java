package me.xepos.rpg.tasks;

import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

public class BeamTask extends BukkitRunnable {
    private final Player beamCaster;
    private final double maxDistance;
    private final Particle particle;
    private final PartySet partySet;

    private double currentRange;

    public BeamTask(Player beamCaster, Particle particle, double maxDistance, PartySet partySet){
        this.beamCaster = beamCaster;
        this.maxDistance = maxDistance;
        this.currentRange = maxDistance;
        this.particle = particle;
        this.partySet = partySet;
    }

    @Override
    public void run() {
        RayTraceResult result = Utils.rayTrace(beamCaster, currentRange, FluidCollisionMode.NEVER);

        LivingEntity target = (LivingEntity) result.getHitEntity();

        if (target == null && result.getHitBlock() == null){
            currentRange = maxDistance;
        }else if (target != null){
            currentRange = target.getLocation().distance(beamCaster.getEyeLocation());
            if (target instanceof Player targetPlayer){ //Potential ally
                if (partySet.isPlayerAllied(beamCaster, targetPlayer)){
                    Utils.healLivingEntity(target, 2.0);
                }else if(partySet.canHurtPlayer(beamCaster, targetPlayer)){
                    targetPlayer.damage(2.0, beamCaster);
                }
            }else if (target instanceof Monster || target instanceof EnderDragon){
                //Enderdragon isn't a monster somehow
                target.damage(2.0, beamCaster);
            }else if (target instanceof Tameable tameable){ //Potential ally's pet
                if (tameable.getOwner() instanceof Player tameableOwner){
                    if (partySet.isPlayerAllied(beamCaster, tameableOwner)){
                        Utils.healLivingEntity(tameable, 2.0);
                    }else if (partySet.canHurtPlayer(beamCaster, tameableOwner)){
                        tameable.damage(2.0, beamCaster);
                    }
                }
            }else{
                Utils.healLivingEntity(target, 2.0);
            }
        }

        //draw particles

    }
}
