package me.xepos.rpg.skills;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Nimble extends XRPGSkill {
    public Nimble(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        //Initializing the modifier
        double multiplier = getSkillVariables().getDouble("speed-multiplier", 1.5) - 1;
        AttributeModifier modifier = new AttributeModifier(UUID.fromString("076c8ed9-b6e2-4da1-a4c0-27c50c61726a"), "NIMBLE_SPRINT", multiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        AttributeModifierManager.getInstance().put(ModifierType.POSITIVE, modifier.getName(), modifier, Attribute.GENERIC_MOVEMENT_SPEED);

        //Making skill have no cooldown
        setRemainingCooldown(-1);

        //Initializing triggers for this skill
        xrpgPlayer.getEventHandler("JUMP").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getEventHandler("SPRINT").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getEventHandler("DAMAGE_TAKEN_ENVIRONMENTAL").addSkill(this.getClass().getSimpleName() ,this);
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

            e.getPlayer().setVelocity(e.getPlayer().getVelocity().multiply(new Vector(1.3, 2, 1.3)));
        }else if(event instanceof EntityDamageEvent && !(event instanceof EntityDamageByEntityEvent)){
            //Reduce damage by half if it's fall damage
            EntityDamageEvent e = (EntityDamageEvent) event;
            if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
            e.setDamage(e.getDamage() / 2);
        }
    }

    @Override
    public void initialize() {

    }
}
