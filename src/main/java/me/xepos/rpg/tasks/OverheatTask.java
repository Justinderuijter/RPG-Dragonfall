package me.xepos.rpg.tasks;

import net.minecraft.server.v1_16_R3.DamageSource;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class OverheatTask extends BukkitRunnable {

    private final LivingEntity target;
    private double damage;

    public OverheatTask(LivingEntity target, double damage, double armorToDamage) {
        this.target = target;
    }

    @Override
    public void run() {

        target.sendMessage(ChatColor.RED + "You've been hit by Overheat!");
        target.sendMessage(ChatColor.RED + "Get in water to reduce the damage!");
        double armorValue = target.getAttribute(Attribute.GENERIC_ARMOR).getValue();

        ArrayList<ItemStack> armor = new ArrayList<ItemStack>() {{
            if (target.getEquipment() != null) {
                add(target.getEquipment().getHelmet());
                add(target.getEquipment().getChestplate());
                add(target.getEquipment().getLeggings());
                add(target.getEquipment().getBoots());
            }
        }};


        int enchantLevel = 0;
        for (ItemStack armorPiece : armor) {
            if (armorPiece != null && armorPiece.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                enchantLevel += armorPiece.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            }
        }
        target.sendMessage("Armor value: " + armorValue);
        target.sendMessage("Protection Level: " + enchantLevel);

        double damage = 5;

        if (!target.isInWater()) {
            damage = (5 + armorValue / 2) * 1 / (1 - enchantLevel * 0.04);
            ((CraftLivingEntity) target).getHandle().damageEntity(DamageSource.FIRE, (float) damage);
        } else {
            ((CraftLivingEntity) target).getHandle().damageEntity(DamageSource.FIRE, (float) damage);
            target.sendMessage("Overheat damage was reduced by the water.");
        }
    }
}
