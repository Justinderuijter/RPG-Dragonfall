package me.xepos.rpg.dependencies.hooks;

import n3kas.ae.api.AEAPI;
import org.bukkit.inventory.ItemStack;

public class AEnchantsHook extends AbstractHook{
    public ItemStack applyEnchantment(ItemStack itemStack, String name, int level){
        if (AEAPI.isAnEnchantment(name)) {
            final int maxLevel = AEAPI.getHighestEnchantmentLevel(name);

            if (level > maxLevel) level = maxLevel;

            return AEAPI.applyEnchant(name, level, itemStack);
        }
        else return itemStack;
    }
}
