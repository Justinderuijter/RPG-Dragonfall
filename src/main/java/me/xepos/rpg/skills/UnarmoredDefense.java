package me.xepos.rpg.skills;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnarmoredDefense extends XRPGPassiveSkill implements IAttributable {
    private boolean isAllLeatherOrAir;

    private final static AttributeModifierManager manager = AttributeModifierManager.getInstance();
    private final static String MVSPD_MOD_NAME = "UNARMORED_DEFENSE_MOVESPEED";
    private final static String ATKSPD_MOD_NAME = "UNARMORED_DEFENSE_ATTACKSPEED";

    public UnarmoredDefense(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        //Add handlers if needed
        if (!xrpgPlayer.getPassiveHandlerList().containsKey("ARMOR_DISPENSE"))
            xrpgPlayer.addPassiveEventHandler("ARMOR_DISPENSE", new PassiveEventHandler());

        if (!xrpgPlayer.getPassiveHandlerList().containsKey("ARMOR_CHANGE"))
            xrpgPlayer.addPassiveEventHandler("ARMOR_CHANGE", new PassiveEventHandler());

        //Register skill
        xrpgPlayer.getPassiveEventHandler("ARMOR_CHANGE").addSkill(this.getClass().getSimpleName(), this);
        xrpgPlayer.getPassiveEventHandler("ARMOR_DISPENSE").addSkill(this.getClass().getSimpleName(), this);

        //Adding to attribute manager
        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(ATKSPD_MOD_NAME)) {
            final double attackSpeedMultiplier = getSkillVariables().getDouble(skillLevel, "attack-speed-multiplier", 1.25) - 1;
            final AttributeModifier attackSpeedMod = new AttributeModifier(UUID.randomUUID(), ATKSPD_MOD_NAME, attackSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

            manager.put(ModifierType.POSITIVE, attackSpeedMod.getName(), attackSpeedMod, Attribute.GENERIC_ATTACK_SPEED);
        }
        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(MVSPD_MOD_NAME)) {
            final double moveSpeedMultiplier = getSkillVariables().getDouble(skillLevel, "move-speed-multiplier", 1.3) - 1;
            final AttributeModifier moveSpeedMod = new AttributeModifier(UUID.randomUUID(), MVSPD_MOD_NAME, moveSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

            manager.put(ModifierType.POSITIVE, moveSpeedMod.getName(), moveSpeedMod, Attribute.GENERIC_MOVEMENT_SPEED);
        }

        //Only executes if the player is already online
        //If the player is not already online, use the IAttributable interface
        if (xrpgPlayer.getPlayer() != null) {
            isAllLeatherOrAir = true;

            for (ItemStack armor : xrpgPlayer.getPlayer().getInventory().getArmorContents()) {
                if (armor == null) continue;
                if (armor.getType() != Material.AIR && !armor.getType().toString().toLowerCase().contains("leather")) {
                    isAllLeatherOrAir = false;
                    break;
                }
            }

            applyEffects(skillLevel);
        }

    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerArmorChangeEvent) {
            PlayerArmorChangeEvent e = (PlayerArmorChangeEvent) event;

            checkIfAllLeatherOrBelow(e.getNewItem());

            applyEffects(getSkillLevel());

        } else if (event instanceof BlockDispenseArmorEvent) {
            BlockDispenseArmorEvent e = (BlockDispenseArmorEvent) event;

            checkIfAllLeatherOrBelow(e.getItem());

            applyEffects(getSkillLevel());
        }

    }

    @Override
    public void initialize() {

    }

    private void checkIfAllLeatherOrBelow(@Nullable ItemStack newItem) {
        isAllLeatherOrAir = true;
        if (newItem.getType() == Material.AIR || newItem.getType().toString().toLowerCase().contains("leather")) {
            for (ItemStack armor : getXRPGPlayer().getPlayer().getInventory().getArmorContents()) {
                if (armor == null) continue;
                if (armor.getType() != Material.AIR && !armor.getType().toString().toLowerCase().contains("leather")) {
                    isAllLeatherOrAir = false;
                    break;
                }

            }
        } else {
            isAllLeatherOrAir = false;
        }
    }

    private void applyEffects(int level){
        if (isAllLeatherOrAir) {
            Utils.addUniqueModifier(getXRPGPlayer().getPlayer(), manager.get(ModifierType.POSITIVE, MVSPD_MOD_NAME));
            if (level >= 2) {
                Utils.addUniqueModifier(getXRPGPlayer().getPlayer(), manager.get(ModifierType.POSITIVE, ATKSPD_MOD_NAME));
            }
        } else {
            Utils.removeUniqueModifier(getXRPGPlayer().getPlayer(), manager.get(ModifierType.POSITIVE, MVSPD_MOD_NAME));
            Utils.removeUniqueModifier(getXRPGPlayer().getPlayer(), manager.get(ModifierType.POSITIVE, ATKSPD_MOD_NAME));
        }
    }

    @Override
    public List<AttributeModifierData> getModifiersToApply() {
        return new ArrayList<AttributeModifierData>()
        {{
            if (manager == null){
                Bukkit.getLogger().severe("Manager is null");
            }
            AttributeModifierData data = manager.get(ModifierType.POSITIVE, MVSPD_MOD_NAME);
            add(data);
            if (getSkillLevel() >= 2) {
                add(manager.get(ModifierType.POSITIVE, ATKSPD_MOD_NAME));
            }
        }};
    }

    @Override
    public void initializeAttributes(int skillLevel) {
        //Adding to attribute manager
//        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(ATKSPD_MOD_NAME)) {
//            final double attackSpeedMultiplier = getSkillVariables().getDouble(skillLevel, "attack-speed-multiplier", 1.25) - 1;
//            final AttributeModifier attackSpeedMod = new AttributeModifier(UUID.randomUUID(), ATKSPD_MOD_NAME, attackSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
//
//            manager.put(ModifierType.POSITIVE, attackSpeedMod.getName(), attackSpeedMod, Attribute.GENERIC_ATTACK_SPEED);
//        }
//        if (!manager.getModifiers(ModifierType.POSITIVE).containsKey(MVSPD_MOD_NAME)) {
//            final double moveSpeedMultiplier = getSkillVariables().getDouble(skillLevel, "move-speed-multiplier", 1.3) - 1;
//            final AttributeModifier moveSpeedMod = new AttributeModifier(UUID.randomUUID(), MVSPD_MOD_NAME, moveSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
//
//            manager.put(ModifierType.POSITIVE, moveSpeedMod.getName(), moveSpeedMod, Attribute.GENERIC_MOVEMENT_SPEED);
//        }
//
//        applyEffects(skillLevel);
    }
}
