package me.xepos.rpg.datatypes;

import me.xepos.rpg.ArmorEffectFactory;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.dependencies.hooks.AEnchantsHook;
import me.xepos.rpg.enums.ArmorSetTriggerType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unused")
public class ArmorSet {
    private final String setId;
    private final String setName;
    private final ConfigurationSection itemSection;
    private final HashMap<Integer, HashMap<ArmorSetTriggerType, ArmorEffect>> effects;
    private int lowestBonus;
    private int highestBonus;

    public ArmorSet(String setId, ConfigurationSection configurationSection){
        this.setId = setId;
        this.setName = configurationSection.getString("name");
        this.itemSection = configurationSection.getConfigurationSection("items");
        this.effects = new HashMap<>();

        ConfigurationSection bonusSection = configurationSection.getConfigurationSection("bonuses");
        if (bonusSection != null) {
            boolean isFirst = true;
            for (String key : bonusSection.getKeys(false)) {
                final int value = Integer.getInteger(key);

                if (isFirst) {
                    this.lowestBonus = value;
                    isFirst = false;
                }
                this.highestBonus = value;

                ConfigurationSection triggerSection = bonusSection.getConfigurationSection(key);

                effects.put(value, ArmorEffectFactory.getTriggers(triggerSection));
            }
        }
    }

    @SuppressWarnings("null")
    public ItemStack generateArmorPiece(String name){
        ConfigurationSection specificItemSection = this.itemSection.getConfigurationSection(name);
        if (specificItemSection != null){
            AEnchantsHook enchantsHook = (AEnchantsHook) XRPG.getInstance().getDependencyManager().getHook("AdvancedEnchantments");
            ItemStack material = new ItemStack(Material.getMaterial(specificItemSection.getString("material", "IRON_HELMET")));
            ItemMeta meta = material.getItemMeta();

            Random random = new Random();

            specificItemSection.getStringList("enchants").forEach(enchantString -> {
                String[] strings = enchantString.split(":");
                int level = 1;
                if (strings[1].startsWith("%") && strings[1].endsWith("%")){
                    String levelRange = strings[1].replaceAll("%", "");
                    String[] minAndMax = levelRange.split("-");

                    final int min = getLowerBound(minAndMax[0]);
                    final int max = getUpperBound(min, minAndMax[1]);

                    level = random.nextInt(max - min) + min;
                }else{
                    level = getPreciseLevel(strings[1]);
                }

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(strings[0]));
                if (enchantment != null){
                    meta.addEnchant(enchantment, level, true);
                }else{
                    if (enchantsHook != null){
                        enchantsHook.applyEnchantment(material, strings[0], level);
                    }
                }
            });
            Component component = Component.text(specificItemSection.getString("name", getSetName() + name));
            meta.displayName(component);

            return material;
        }
        return new ItemStack(Material.AIR);
    }

    public boolean canTrigger(int number){
        return number >= lowestBonus;
    }

    public Set<String> getValidPieces(){
        return new HashSet<>(itemSection.getKeys(false));
    }

    public String getSetId() {
        return setId;
    }

    public String getSetName() {
        return setName;
    }

    public int getLowestBonus() {
        return lowestBonus;
    }

    public int getHighestBonus() {
        return highestBonus;
    }

    @Nullable
    public HashMap<ArmorSetTriggerType, ArmorEffect> getEffectsForLevel(int level){
        if (level > highestBonus) return effects.get(highestBonus);
        else return effects.get(level);
    }

    private int getLowerBound(String lower){
        Integer lowerBound = Integer.getInteger(lower);

        if (lowerBound == null || lowerBound < 0) return 1;

        return lowerBound;
    }

    private int getUpperBound(int lowerBound, String upper){
        Integer upperBound = Integer.getInteger(upper);

        if (upperBound == null || upperBound < lowerBound) return lowerBound;

        return upperBound;
    }

    private int getPreciseLevel(String level){
        Integer intLevel = Integer.getInteger(level);

        if (intLevel == null || intLevel > 1) return 1;

        return intLevel;
    }

}
