package me.xepos.rpg.skills;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Nimble extends XRPGPassiveSkill {
    private static final String speedAttributeName = "NIMBLE_";
    public Nimble(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        //Initializing the modifier
        double multiplier = getSkillVariables().getDouble(skillLevel, "speed-multiplier", 1.5) - 1;
        AttributeModifier modifier = new AttributeModifier(UUID.fromString("076c8ed9-b6e2-4da1-a4c0-27c50c61726a"), speedAttributeName + skillLevel, multiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        AttributeModifierManager.getInstance().put(ModifierType.POSITIVE, modifier.getName(), modifier, Attribute.GENERIC_MOVEMENT_SPEED);

        //Making skill have no cooldown
        setRemainingCooldown(-1);

        //Initializing triggers for this skill
        xrpgPlayer.getPassiveEventHandler("JUMP").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getPassiveEventHandler("SNEAK").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getPassiveEventHandler("SPRINT").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN_ENVIRONMENTAL").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerToggleSprintEvent){
            AttributeModifierManager manager = AttributeModifierManager.getInstance();
            PlayerToggleSprintEvent e = (PlayerToggleSprintEvent) event;

            if (e.isSprinting()){
                Utils.addUniqueModifier(e.getPlayer(), manager.get(ModifierType.POSITIVE, "NIMBLE_SPRINT"));
            }else{
                Utils.removeUniqueModifier(e.getPlayer(), manager.get(ModifierType.POSITIVE, "NIMBLE_SPRINT"));
            }

        }else if(event instanceof PlayerJumpEvent){
            PlayerJumpEvent e = (PlayerJumpEvent) event;

            double vertical = getSkillVariables().getDouble(getSkillLevel(), "vertical-velocity-multiplier", 2.1);
            double horizontal = getSkillVariables().getDouble(getSkillLevel(), "horizontal-velocity-multiplier", 1.5);

            e.getPlayer().setVelocity(e.getPlayer().getVelocity().multiply(new Vector(horizontal, vertical, horizontal)));
        }else if(event instanceof EntityDamageEvent && !(event instanceof EntityDamageByEntityEvent)){
            //Reduce damage by half if it's fall damage
            EntityDamageEvent e = (EntityDamageEvent) event;
            if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
            double damage = e.getDamage() / (100 / getSkillVariables().getDouble(getSkillLevel(), "fall-damage-reduction", 50.0));
            if (damage < 1) {
                e.setCancelled(true);
            } else {
                e.setDamage(damage);
            }
        } else if(event instanceof PlayerToggleSneakEvent){
            PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
            AttributeModifierManager manager = AttributeModifierManager.getInstance();

            if (!e.getPlayer().isSneaking()){
                //Enter sneak
                Utils.removeUniqueModifier(e.getPlayer(), manager.get(ModifierType.POSITIVE, "NIMBLE_SPRINT"));
            }else{
                //Leaving sneak
                if (e.getPlayer().isSprinting()){
                    Utils.addUniqueModifier(e.getPlayer(), manager.get(ModifierType.POSITIVE, "NIMBLE_SPRINT"));
                }
            }
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void setSkillLevel(int skillLevel) {
        super.setSkillLevel(skillLevel);

        AttributeModifierManager manager = AttributeModifierManager.getInstance();

        if (manager.get(ModifierType.POSITIVE, speedAttributeName + skillLevel) == null){
            double multiplier = getSkillVariables().getDouble(skillLevel, "speed-multiplier", 1.5) - 1;
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), speedAttributeName + skillLevel, multiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

            AttributeModifierManager.getInstance().put(ModifierType.POSITIVE, modifier.getName(), modifier, Attribute.GENERIC_MOVEMENT_SPEED);
        }
    }
}
