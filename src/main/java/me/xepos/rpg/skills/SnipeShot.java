package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SnipeShot extends XRPGPassiveSkill {
    public SnipeShot(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        setRemainingCooldown(-1);
        xrpgPlayer.getPassiveEventHandler("SHOOT_BOW").addSkill(this.getClass().getSimpleName() ,this);
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
        doSnipeShot(e, (Arrow) e.getProjectile());
    }

    @Override
    public void initialize() {

    }

    private void doSnipeShot(EntityShootBowEvent e, Arrow arrow) {
        final int pierce = getSkillVariables().getInt("pierce", 0);
        final float force = e.getForce();

        if (force >= 0.95){
            arrow.setGravity(false);
            arrow.setPierceLevel(arrow.getPierceLevel() + pierce);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

            ProjectileData data = new ProjectileData(arrow, 0, 20);
            data.setHeadshotDamage(getSkillVariables().getDouble("headshot-multiplier"));

            getPlugin().projectiles.put(arrow.getUniqueId(), data);

            new BukkitRunnable() {
                @Override
                public void run() {
                    arrow.remove();
                }
            }.runTaskLater(getPlugin(), (int) (force * 300));
        }


    }
}
