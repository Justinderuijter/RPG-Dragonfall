package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class SoulShot extends XRPGBowSkill {
    public SoulShot(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("SHOOT_BOW").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent)) return;
        EntityShootBowEvent e = (EntityShootBowEvent) event;
        if (!(e.getProjectile() instanceof Arrow)) return;

        if (!isSkillReady()) {
            e.getEntity().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }
        Arrow arrow = (Arrow) e.getProjectile();
        arrow.setDamage(0);

        double damageMultiplier = 1 - getSkillVariables().getDouble("percent-health-damage", 0.25);

        getPlugin().projectiles.put(arrow.getUniqueId(), new ProjectileData(arrow, damageMultiplier, 20));
        setRemainingCooldown(getCooldown());
    }

    @Override
    public void initialize() {

    }
}
