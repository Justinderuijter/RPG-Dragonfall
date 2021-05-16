package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class StaggeringPush extends XRPGActiveSkill {
    public StaggeringPush(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
        Player player = e.getPlayer();
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        RayTraceResult result = Utils.rayTrace(player, 6, FluidCollisionMode.NEVER);

        if (result != null && result.getHitEntity() != null){
            LivingEntity target = (LivingEntity) result.getHitEntity();
            Vector unitVector = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

            target.setVelocity(unitVector.multiply(1.5));
            target.damage(getDamage(), player);
        }
    }


    @Override
    public void initialize() {

    }
}
