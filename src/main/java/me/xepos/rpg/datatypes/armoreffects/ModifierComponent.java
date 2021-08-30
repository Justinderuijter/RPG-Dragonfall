package me.xepos.rpg.datatypes.armoreffects;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.util.UUID;

public class ModifierComponent implements IEffectComponent, IModifierHolder {

    final String modifierName;
    final boolean add;
    public ModifierComponent(String effect){
        String[] args = effect.split(":");

        add = args[0].toUpperCase().startsWith("ADD");

        modifierName = XRPG.modifierPrefix + args[1];
        AttributeModifierManager manager = AttributeModifierManager.getInstance();
        if (manager.get(ModifierType.POSITIVE, modifierName) == null){
            try{
                Attribute attribute = Attribute.valueOf(args[2]);
                double amount = Double.parseDouble(args[3]);

                AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                if (args.length > 4 && args[5].equalsIgnoreCase("ADD")){
                    operation = AttributeModifier.Operation.ADD_NUMBER;
                }

                manager.put(ModifierType.POSITIVE, modifierName, new AttributeModifier(UUID.randomUUID(), modifierName, amount, operation), attribute);
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
    }


    @Override
    public void activate(Event event) {
        if (event instanceof PlayerEvent e){
            applyEffect(e.getPlayer());
        }
    }

    private void applyEffect(LivingEntity livingEntity){
        AttributeModifierData data = AttributeModifierManager.getInstance().get(ModifierType.POSITIVE, modifierName);
        if (add){
            Bukkit.getLogger().info("Adding modifier: " + modifierName);
            Utils.addUniqueModifier(livingEntity, data);
        }else {
            Bukkit.getLogger().info("removing modifier: " + modifierName);
            Utils.removeUniqueModifier(livingEntity, data);
        }
    }


    @Override
    public void applyModifiers(LivingEntity livingEntity) {
        AttributeModifierData data = AttributeModifierManager.getInstance().get(ModifierType.POSITIVE, modifierName);
        Utils.addUniqueModifier(livingEntity, data);
    }

    @Override
    public void removeModifiers(LivingEntity livingEntity) {
        AttributeModifierData data = AttributeModifierManager.getInstance().get(ModifierType.POSITIVE, modifierName);
        Utils.removeUniqueModifier(livingEntity, data);
    }

    @Override
    public void activate(LivingEntity livingEntity) {
        livingEntity.sendMessage("Activating livingEntity overload method");
        Bukkit.getLogger().info("Activating livingEntity overload method");
        applyEffect(livingEntity);
    }
}
