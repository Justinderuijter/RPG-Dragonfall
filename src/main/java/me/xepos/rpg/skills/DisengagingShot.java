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
import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;

public class DisengagingShot extends XRPGBowSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DISPLACEMENT};

    public DisengagingShot(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityShootBowEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

        if (!isSkillReady()){
            e.getEntity().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        if (e.getProjectile() instanceof Arrow arrow) {

            ProjectileData data = new ProjectileData(arrow, getXRPGPlayer().getLevel(), 10);
            data.setDisengage(true);
            getPlugin().projectiles.put(e.getProjectile().getUniqueId(), data);

            setRemainingCooldown(getCooldown());
            updatedCasterMana();

            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                getPlugin().projectiles.remove(arrow.getUniqueId());
            }, (long)(getSkillVariables().getDouble(getSkillLevel(), "max-travel-time", 1.0) * 20));
        }
    }

    @Override
    public void initialize() {

    }
}
