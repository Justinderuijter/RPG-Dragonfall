package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

public class RapidFire extends XRPGBowSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DAMAGE};

    public RapidFire(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

        if (!isSkillReady()) {
            e.getEntity().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        Arrow arrow = (Arrow) e.getProjectile();
        double damage = arrow.getDamage();
        final Vector velocity = arrow.getVelocity();

        arrow.setDamage(0);

        ProjectileData data = new ProjectileData(arrow, getXRPGPlayer().getLevel(), damage, 20);
        getPlugin().projectiles.put(arrow.getUniqueId(), data);

        setRemainingCooldown(getCooldown());
        updatedCasterMana();

        for (int i = 0; i < getSkillVariables().getInt(getSkillLevel(), "extra-arrows"); i++) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                Arrow extraArrow = e.getEntity().launchProjectile(Arrow.class, e.getProjectile().getLocation().getDirection());
                extraArrow.setVelocity(velocity);
                extraArrow.setDamage(0);
                extraArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

                ProjectileData extraData = new ProjectileData(extraArrow, getXRPGPlayer().getLevel(), damage, 20);
                getPlugin().projectiles.put(extraArrow.getUniqueId(), extraData);
            }, (i + 1) * 5L);

        }



    }

    @Override
    public void initialize() {

    }
}
