package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class LeapOfFaith extends XRPGSkill {
    public LeapOfFaith(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
        xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;

        if (!isSkillReady()){
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        RayTraceResult result = Utils.rayTrace(e.getPlayer(), 16, FluidCollisionMode.NEVER);

        if (result.getHitEntity() != null){
            LivingEntity entity = (LivingEntity) result.getHitEntity();
            Location loc = entity.getLocation();
            Location targetLoc = e.getPlayer().getLocation();
            double pullForce = getSkillVariables().getDouble("pull-force", 1.0);

            double x = loc.getX() - targetLoc.getX();
            double y = loc.getY() - targetLoc.getY();
            double z = loc.getZ() - targetLoc.getZ();
            Vector velocity = new Vector(x, y, z).normalize().multiply(-pullForce);
            entity.setVelocity(velocity);

            setRemainingCooldown(getCooldown());
        }

    }

    @Override
    public void initialize() {

    }
}
