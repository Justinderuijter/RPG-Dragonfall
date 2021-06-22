package me.xepos.rpg.skills;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.IAttributable;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HealthBoost extends XRPGPassiveSkill implements IAttributable {

    private static final String healthAttributeName = "HEALTH_BOOST_HEALTH_";
    private static final String armorAttributeName = "HEALTH_BOOST_ARMOR_";
    private static double healthPerLevel = -1;
    private static double armorPerLevel = -1;

    public HealthBoost(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        if (armorPerLevel == -1) armorPerLevel = skillVariables.getDouble(getSkillLevel(),"armor-per-level", 2);
        if (healthPerLevel == -1) healthPerLevel = skillVariables.getDouble(getSkillLevel(), "health-per-level", 2);

        final AttributeModifierManager manager = AttributeModifierManager.getInstance();

        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(healthAttributeName + getSkillLevel())){
            final double healthAmount = healthPerLevel * skillLevel;
            final AttributeModifier healthMod = new AttributeModifier(UUID.randomUUID(), healthAttributeName + skillLevel, healthAmount, AttributeModifier.Operation.ADD_NUMBER);

            manager.put(ModifierType.POSITIVE, healthMod.getName(), healthMod, Attribute.GENERIC_MAX_HEALTH);
        }
        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(armorAttributeName + getSkillLevel())){
            final double armorAmount = armorPerLevel * skillLevel;
            final AttributeModifier armorMod = new AttributeModifier(UUID.randomUUID(), armorAttributeName + skillLevel, armorAmount, AttributeModifier.Operation.ADD_NUMBER);


            manager.put(ModifierType.POSITIVE, armorMod.getName(), armorMod, Attribute.GENERIC_ARMOR);
        }

        if (xrpgPlayer.getPlayer() != null){
            Utils.addUniqueModifier(xrpgPlayer.getPlayer(), manager.get(ModifierType.POSITIVE, healthAttributeName + skillLevel));
            Utils.addUniqueModifier(xrpgPlayer.getPlayer(), manager.get(ModifierType.POSITIVE, armorAttributeName + skillLevel));
        }

        if (!xrpgPlayer.getPassiveHandlerList().containsKey("ATTRIBUTE")){
            xrpgPlayer.addPassiveEventHandler("ATTRIBUTE", new PassiveEventHandler());
        }
        xrpgPlayer.getPassiveEventHandler("ATTRIBUTE").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void setSkillLevel(int skillLevel){
        if (getSkillLevel() == skillLevel) return;

        final Player player = getXRPGPlayer().getPlayer();
        AttributeModifierManager manager = AttributeModifierManager.getInstance();

        Utils.removeUniqueModifier(player, manager.get(ModifierType.POSITIVE, healthAttributeName + getSkillLevel()));
        Utils.removeUniqueModifier(player, manager.get(ModifierType.POSITIVE, armorAttributeName + getSkillLevel()));

        super.setSkillLevel(skillLevel);

        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(healthAttributeName + getSkillLevel())){
            final double healthAmount = healthPerLevel * skillLevel;
            final AttributeModifier healthMod = new AttributeModifier(UUID.randomUUID(), healthAttributeName + skillLevel, healthAmount, AttributeModifier.Operation.ADD_NUMBER);

            manager.put(ModifierType.POSITIVE, healthMod.getName(), healthMod, Attribute.GENERIC_MAX_HEALTH);
        }
        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(armorAttributeName + getSkillLevel())){
            final double armorAmount = armorPerLevel * skillLevel;
            final AttributeModifier armorMod = new AttributeModifier(UUID.randomUUID(), armorAttributeName + skillLevel, armorAmount, AttributeModifier.Operation.ADD_NUMBER);

            manager.put(ModifierType.POSITIVE, armorMod.getName(), armorMod, Attribute.GENERIC_ARMOR);
        }

        Utils.addUniqueModifier(player, manager.get(ModifierType.POSITIVE, healthAttributeName + getSkillLevel()));
        Utils.addUniqueModifier(player, manager.get(ModifierType.POSITIVE, armorAttributeName + getSkillLevel()));
    }

    @Override
    public List<AttributeModifierData> getModifiersToApply() {
        final AttributeModifierManager manager = AttributeModifierManager.getInstance();

        return new ArrayList<AttributeModifierData>(){{
            add(manager.get(ModifierType.POSITIVE, healthAttributeName + getSkillLevel()));
            add(manager.get(ModifierType.POSITIVE, armorAttributeName + getSkillLevel()));
        }};
    }
}
