package me.xepos.rpg.datatypes;

import me.xepos.rpg.ArmorEffectFactory;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.dependencies.hooks.AEnchantsHook;
import me.xepos.rpg.enums.ArmorSetTriggerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.*;

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
                Bukkit.getLogger().warning("key: " + key);
                final int value = Integer.parseInt(key);

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

            Random random = new Random();

            //Filtering vanilla enchants from AE enchants
            HashMap<Enchantment, Integer> vanillaEnchants = new HashMap<>();
            for (String enchantString:specificItemSection.getStringList("enchants")) {
                String[] strings = enchantString.split(":");
                int level = 1;
                if (strings[1].startsWith("%") && strings[1].endsWith("%")){
                    String levelRange = strings[1].replaceAll("%", "");
                    String[] minAndMax = levelRange.split("-");

                    final int min = getLowerBound(minAndMax[0]);
                    final int max = getUpperBound(min, minAndMax[1]);

                    Bukkit.getLogger().info(strings[0] + ": " + min + ".." + max);

                    //Must be positive error
                    level = random.nextInt(max - min) + min;
                }else{
                    level = getPreciseLevel(strings[1]);
                }

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(strings[0]));
                if (enchantment != null){
                    vanillaEnchants.put(enchantment, level);
                }else{
                    if (enchantsHook != null){
                        Bukkit.getLogger().info("Applied: " + strings[0]);
                        material = enchantsHook.applyEnchantment(material, strings[0], level);
                    }
                }
            }

            //Applying filtered vanilla enchants
            ItemMeta meta = material.getItemMeta();
            for (Map.Entry<Enchantment, Integer> vanillaEntry:vanillaEnchants.entrySet()) {
                meta.addEnchant(vanillaEntry.getKey(), vanillaEntry.getValue(), true);
                Bukkit.getLogger().info("Applied: " + vanillaEntry.getKey().getName() + " " + vanillaEntry.getValue());
            }

            meta.setDisplayName(specificItemSection.getString("name", getSetName() + name));
            List<String> lore = specificItemSection.getStringList("lore");
            lore.addAll(0, meta.getLore() == null ? Collections.emptyList() : meta.getLore());
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey(XRPG.getInstance(), "set"), PersistentDataType.STRING, this.setId);
            material.setItemMeta(meta);


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
        int lowerBound = 1;
        try {
            lowerBound = Integer.parseInt(lower);
        }catch (NumberFormatException ignore){
            Bukkit.getLogger().warning(lower + " is not a number! using 1");
        }

        if (lowerBound < 0) return 1;

        return lowerBound;
    }

    private int getUpperBound(int lowerBound, String upper){
        final int upperBound = Integer.parseInt(upper);

        if (upperBound < lowerBound) return lowerBound;

        return upperBound;
    }

    private int getPreciseLevel(String level){
        int intLevel = Integer.parseInt(level);

        if (intLevel < 1) return 1;

        return intLevel;
    }

}
