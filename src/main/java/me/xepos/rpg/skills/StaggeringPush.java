package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class StaggeringPush extends XRPGSkill {
    public StaggeringPush(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getDamager();
        if (!player.isSprinting()) return;
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        LivingEntity target = (LivingEntity) e.getEntity();

        Vector unitVector = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

        target.setVelocity(unitVector.multiply(1.5));
        target.damage(getDamage(), player);
    }


    @Override
    public void initialize() {

    }
}
