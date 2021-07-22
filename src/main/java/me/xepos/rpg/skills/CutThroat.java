package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class CutThroat extends XRPGPassiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.PASSIVE, SpellType.DAMAGE};


    public CutThroat(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        LivingEntity entity = (LivingEntity) e.getEntity();

        if (e.getEntity() instanceof LivingEntity) {
            XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
            Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

            if (spellCastEvent.isCancelled()) return;

            Vector attackerDirection = e.getDamager().getLocation().getDirection();
            Vector victimDirection = e.getEntity().getLocation().getDirection();
            //determine if the dot product between the vectors is greater than 0
            //If it is, we can conclude that the attack was a backstab
            if (attackerDirection.dot(victimDirection) > 0) {
                if (entity.getHealth() <= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / (100 / getSkillVariables().getDouble(getSkillLevel(), "threshold", 25.0)) && Utils.isSkillReady(getRemainingCooldown())) {
                    entity.setHealth(0.0);
                    if (entity instanceof Player) {
                        e.getDamager().getWorld().getNearbyEntities(e.getDamager().getLocation(), 10, 5, 10, p -> p instanceof Player).forEach(p -> p.sendMessage(entity.getName() + " was executed by " + e.getDamager().getName() + "!"));
                    }
                    setRemainingCooldown(getCooldown());

                } else {
                    double finalDmg = e.getDamage() * getDamageMultiplier();
                    e.setDamage(finalDmg);
                    e.getDamager().sendMessage("Backstrike dealt " + finalDmg + " damage!");
                }
            }
        }

    }

    @Override
    public void initialize() {

    }
}
