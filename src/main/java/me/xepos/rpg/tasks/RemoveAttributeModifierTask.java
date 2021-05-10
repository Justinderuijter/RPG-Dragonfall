package me.xepos.rpg.tasks;

import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;


public class RemoveAttributeModifierTask extends BukkitRunnable {

    private final LivingEntity livingEntity;
    private final Attribute attribute;
    private final AttributeModifier modifier;

    public RemoveAttributeModifierTask(LivingEntity livingEntity, Attribute attribute, AttributeModifier modifier)
    {
        this.livingEntity = livingEntity;
        this.attribute = attribute;
        this.modifier = modifier;
    }

    @Override
    public void run(){
        Utils.removeUniqueModifier(livingEntity, attribute, modifier);
    }
}
