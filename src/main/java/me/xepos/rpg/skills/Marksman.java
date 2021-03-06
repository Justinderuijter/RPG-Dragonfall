package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Marksman extends XRPGPassiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.PASSIVE, SpellType.DAMAGE};

    public Marksman(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        setRemainingCooldown(-1);
        xrpgPlayer.getPassiveEventHandler("SHOOT_BOW").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent e)) return;
        if (!(e.getProjectile() instanceof Arrow)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

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
        final int pierce = getSkillVariables().getInt(getSkillLevel(), "pierce", 0);
        final float force = e.getForce();

        if (force >= 0.95){
            arrow.setGravity(false);
            arrow.setPierceLevel(arrow.getPierceLevel() + pierce);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

            ProjectileData data = new ProjectileData(arrow, 0, 20);
            data.setHeadshotDamage((100 + getSkillVariables().getDouble(getSkillLevel(), "headshot-multiplier")) / 100);

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
