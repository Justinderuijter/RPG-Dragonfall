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

    private static final String healthAttributeName = XRPG.modifierPrefix + "HEALTH_BOOST_HEALTH_";
    private static final String armorAttributeName = XRPG.modifierPrefix + "HEALTH_BOOST_ARMOR_";
    private static double healthPerLevel = -1;
    private static double armorPerLevel = -1;

    public HealthBoost(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        if (armorPerLevel == -1) armorPerLevel = skillVariables.getDouble(getSkillLevel(),"armor-per-level", 2);
        if (healthPerLevel == -1) healthPerLevel = skillVariables.getDouble(getSkillLevel(), "health-per-level", 2);

        final AttributeModifierManager manager = AttributeModifierManager.getInstance();

        registerAttributes(manager, skillLevel);

        if (xrpgPlayer.getPlayer() != null){
            Utils.addUniqueModifier(xrpgPlayer.getPlayer(), manager.get(ModifierType.POSITIVE, healthAttributeName + skillLevel));
            Utils.addUniqueModifier(xrpgPlayer.getPlayer(), manager.get(ModifierType.POSITIVE, armorAttributeName + skillLevel));
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

        registerAttributes(manager, skillLevel);

        Utils.addUniqueModifier(player, manager.get(ModifierType.POSITIVE, healthAttributeName + getSkillLevel()));
        Utils.addUniqueModifier(player, manager.get(ModifierType.POSITIVE, armorAttributeName + getSkillLevel()));
    }

    @Override
    public List<AttributeModifierData> getModifiersToApply() {
        final AttributeModifierManager manager = AttributeModifierManager.getInstance();

        registerAttributes(manager, getSkillLevel());

        return new ArrayList<AttributeModifierData>(){{
            add(manager.get(ModifierType.POSITIVE, healthAttributeName + getSkillLevel()));
            add(manager.get(ModifierType.POSITIVE, armorAttributeName + getSkillLevel()));
        }};
    }

    @Override
    public void registerAttributes(AttributeModifierManager attributeModifierManager, int skillLevel) {

        if (!attributeModifierManager.getModifiers(ModifierType.POSITIVE).containsKey(healthAttributeName + skillLevel)){
            final double healthAmount = healthPerLevel * getSkillLevel();
            final AttributeModifier healthMod = new AttributeModifier(UUID.randomUUID(), healthAttributeName + getSkillLevel(), healthAmount, AttributeModifier.Operation.ADD_NUMBER);

            attributeModifierManager.put(ModifierType.POSITIVE, healthMod.getName(), healthMod, Attribute.GENERIC_MAX_HEALTH);
        }
        if (!attributeModifierManager.getModifiers(ModifierType.POSITIVE).containsKey(armorAttributeName + getSkillLevel())){
            final double armorAmount = armorPerLevel * getSkillLevel();
            final AttributeModifier armorMod = new AttributeModifier(UUID.randomUUID(), armorAttributeName + getSkillLevel(), armorAmount, AttributeModifier.Operation.ADD_NUMBER);


            attributeModifierManager.put(ModifierType.POSITIVE, armorMod.getName(), armorMod, Attribute.GENERIC_ARMOR);
        }

        if (!getXRPGPlayer().getPassiveHandlerList().containsKey("ATTRIBUTE")){
            getXRPGPlayer().addPassiveEventHandler("ATTRIBUTE", new PassiveEventHandler());
        }
    }
}
