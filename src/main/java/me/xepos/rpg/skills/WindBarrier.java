package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WindBarrier extends XRPGPassiveSkill {
    public WindBarrier(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getEntity();

        if (player.getHealth() <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / (100 / getSkillVariables().getDouble(getSkillLevel(), "threshold", 50.0))) {
            if (e.getDamager() instanceof Projectile || e.getDamager() instanceof Explosive) {
                if (isSkillReady()) {
                    e.setCancelled(true);
                    setRemainingCooldown(getCooldown());
                    player.sendMessage(Utils.getPassiveCooldownMessage(getSkillName(), getRemainingCooldown()));
                }
            }
        }
    }

    @Override
    public void initialize() {

    }
}
