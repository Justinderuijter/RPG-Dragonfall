package me.xepos.rpg.tasks;

import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class OverheatTask extends BukkitRunnable {

    private final LivingEntity target;
    private double damage;
    private final double armorDamageRatio;

    public OverheatTask(LivingEntity target, double damage, double armorToDamage) {
        this.target = target;
        this.damage = damage;
        this.armorDamageRatio = armorToDamage;
    }

    @Override
    public void run() {
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

        target.setVisualFire(false);
        target.setNoDamageTicks(0);

        if (!target.isInWater()) {
            damage = (damage + armorValue * armorDamageRatio) * 1 / (1 - enchantLevel * 0.04);
            ((CraftLivingEntity) target).getHandle().damageEntity(DamageSource.a, (float) damage);
        } else {
            ((CraftLivingEntity) target).getHandle().damageEntity(DamageSource.a, (float) damage);
            target.sendMessage(ChatColor.GREEN + "Overheat damage was reduced by the water.");
        }
    }
}
