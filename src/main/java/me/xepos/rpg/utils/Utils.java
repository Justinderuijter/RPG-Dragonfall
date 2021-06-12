package me.xepos.rpg.utils;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.enums.ModifierType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class Utils {

    public static boolean isItemNameMatching(ItemStack itemStack, String itemName) {
        ItemMeta metaData = itemStack.getItemMeta();
        if (metaData != null && metaData.hasDisplayName()) {
            return metaData.getDisplayName().equals(itemName);
        }
        return false;
    }

    public static boolean isItemLoreMatching(ItemStack itemStack, String loreToCheck) {
        ItemMeta metaData = itemStack.getItemMeta();
        if (metaData.hasLore()) {
            List<String> lore = metaData.getLore();
            if (lore.contains(loreToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isItemLoreAndNameMatching(ItemStack itemStack, String loreToCheck, String itemName) {
        ItemMeta metaData = itemStack.getItemMeta();
        if (metaData.hasLore() && metaData.hasDisplayName()) {
            List<String> lore = metaData.getLore();
            if (lore != null && lore.contains(loreToCheck)
                    && metaData.getDisplayName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    //Poor man's raycast
    public static Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    public static LivingEntity getTargetLivingEntity(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        LivingEntity livingEntity = null;
        while (iter.hasNext()) {
            lastBlock = iter.next();
            List<Entity> entities = (List<Entity>) lastBlock.getWorld().getNearbyEntities(lastBlock.getLocation(), 1, 1, 1);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity && entity != player) {
                    livingEntity = (LivingEntity) entity;
                    break;
                }
            }
            if (lastBlock.getType() == Material.AIR && livingEntity == null) {
                continue;
            }
            break;
        }
        return livingEntity;
    }

    public static List<LivingEntity> getLivingEntitiesInLine(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        List<Entity> entities = new ArrayList<>();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR || lastBlock.getType() == Material.BAMBOO || lastBlock.getType() == Material.WATER) {
                entities.addAll(lastBlock.getWorld().getNearbyEntities(lastBlock.getLocation(), 2, 2, 2));
                continue;
            }
            break;
        }
        List<LivingEntity> livingEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                //Filter out dupes
                if (!livingEntities.contains((LivingEntity) entity)) {
                    livingEntities.add((LivingEntity) entity);
                }

            }
        }
        return livingEntities;
    }

    /**
     * Gets a random LivingEntity in specified range.
     * If world PvP is disabled, this will excluded players entirely.
     *
     * @param originalEntity  The entity that was hit originally, well be used as the center of the area from which to fetch entities
     * @param xz              The range for x and z box, based on location (horizontal axes)
     * @param y               The range for y, based on location (vertical axis)
     * @param excludedEntity  Player that is excluded from the possible return values, usually the attacker/shooter
     * @param ignoreVillagers If true, villagers will be excluded from possible return values
     * @return a random LivingEntity in specified range, null if no entities were found
     */
    public static LivingEntity getRandomLivingEntity(Entity originalEntity, double xz, double y, Entity excludedEntity, boolean ignoreVillagers) {

        Location location = originalEntity.getLocation();
        List<Entity> entities = new ArrayList<>(location.getWorld().getNearbyEntities(location, xz, y, xz, p -> p instanceof LivingEntity && p != originalEntity && p != excludedEntity));

        Iterator<Entity> e = entities.iterator();
        while (e.hasNext()) {
            Entity entity = e.next();

            if ((entity instanceof Villager ? !ignoreVillagers : !(entity instanceof Player) || (location.getWorld().getPVP() && entity != excludedEntity))) {
                Vector vector = entity.getLocation().toVector().subtract(location.toVector());
                RayTraceResult result = location.getWorld().rayTrace(location, vector, 11.0, FluidCollisionMode.NEVER, true, 0.9, p -> p instanceof LivingEntity && p != originalEntity);
                if (result != null && result.getHitEntity() == null)
                    e.remove();
            } else {
                e.remove();
            }
        }
        if (entities.size() > 0) {
            Random rnd = new Random();
            return (LivingEntity) entities.get(rnd.nextInt(entities.size()));
        }
        return null;
    }

    public static void healLivingEntity(LivingEntity entity, double amount) {
        double maxHP = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double currentHP = entity.getHealth();

        if (currentHP <= maxHP - amount) {
            entity.setHealth(currentHP + amount);
        } else {
            entity.setHealth(currentHP + maxHP - currentHP);
        }
    }

    public static void decreaseHealth(LivingEntity entity, double amount) {
        if (entity.getHealth() >= amount)
            entity.setHealth(entity.getHealth() - amount);
        else
            entity.setHealth(0);
    }

    public static boolean isSkillReady(Long lastUsage) {
        return lastUsage <= System.currentTimeMillis();
    }

    public static String getPassiveCooldownMessage(String skillName, double cooldown) {
        return ChatColor.RED + skillName + " is now on cooldown for " + cooldown + " seconds";
    }

    public static String getCooldownMessage(String skillName, long lastUsage) {
        return ChatColor.RED + skillName + " is still on cooldown for " + (lastUsage - System.currentTimeMillis()) / 1000 + " seconds";
    }

    /**
     * Add a modifier to the entity.
     * If the entity attribute already contains this modifier, this method will not do anything.
     *
     * @param entity:    The entity the modifier will be applied to.
     * @param attribute: The attribute that will get modified.
     * @param modifier:  AttributeModifier containing the data for the modification
     * @return true if the modifier was applied, if not this returns false.
     */
    @SuppressWarnings("all")
    public static boolean addUniqueModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        if (!entity.getAttribute(attribute).getModifiers().contains(modifier)) {
            entity.getAttribute(attribute).addModifier(modifier);
            return true;
        }
        return false;
    }

    /**
     * Add a modifier to the entity.
     * If the entity attribute already contains this modifier, this method will not do anything.
     *
     * @param entity:       The entity the modifier will be applied to.
     * @param modifierData: The data for the modifier, contains the modifier itself but also the attribute it will be applied to.
     * @return true if the modifier was applied, if not this returns false.
     */
    @SuppressWarnings("all")
    public static boolean addUniqueModifier(LivingEntity entity, AttributeModifierData modifierData) {
        if (modifierData == null || modifierData.getAttributeModifier() == null || modifierData.getAttribute() == null){
            Bukkit.getLogger().warning("Error loading modifier");
            return false;
        }

        if (!entity.getAttribute(modifierData.getAttribute()).getModifiers().contains(modifierData.getAttributeModifier())) {
            entity.getAttribute(modifierData.getAttribute()).addModifier(modifierData.getAttributeModifier());
            return true;
        }
        return false;
    }

    /**
     * Removes a modifier from a entity if they have it.
     *
     * @param entity:    The entity the modifier will be removed from.
     * @param attribute: The attribute from which the modifier will be removed.
     * @param modifier:  AttributeModifier containing the data for the modification.
     * @return true if the modifier found and removed, if not this returns false.
     */
    @SuppressWarnings("all")
    public static boolean removeUniqueModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        if (entity.getAttribute(attribute).getModifiers().contains(modifier)) {
            //entity.getAttribute(attribute).getModifiers().removeIf(p -> p.getUniqueId().equals("8325e13e-f638-4c8f-835f-0ee7d58c6513"));
            entity.getAttribute(attribute).removeModifier(modifier);
            return true;
        }
        return false;
    }

    /**
     * Removes a modifier from a entity if they have it.
     *
     * @param entity:      The entity the modifier will be removed from.
     * @param modifierData : Data contain the modifier and the attribute that is being modified
     * @return true if the modifier found and removed, if not this returns false.
     */
    @SuppressWarnings("all")
    public static boolean removeUniqueModifier(LivingEntity entity, AttributeModifierData modifierData) {
        if (modifierData == null || modifierData.getAttributeModifier() == null || modifierData.getAttribute() == null){
            return false;
        }

        if (entity.getAttribute(modifierData.getAttribute()).getModifiers().contains(modifierData.getAttributeModifier())) {
            entity.getAttribute(modifierData.getAttribute()).removeModifier(modifierData.getAttributeModifier());
            return true;
        }
        return false;
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

    @SuppressWarnings("all")
    public static String enumTypeFormatter(String input) {

        String[] strings = input.split("_");

        StringBuilder result = new StringBuilder();
        for (String string : strings) {
            result.append(StringUtils.capitalize(string.toLowerCase()) + " ");
        }

        return result.toString().trim();

    }

    public static String enumTypeFormatter(String input, @Nullable String splitString) {
        if (splitString != null && !splitString.equals("")) {
            if (input.contains(splitString)) {
                String[] strings = input.split(splitString);

                StringBuilder result = new StringBuilder();
                for (String string : strings) {
                    result.append(StringUtils.capitalize(string.toLowerCase()));
                }

                return result.toString();
            }
        }
        return StringUtils.capitalize(input.toLowerCase());
    }

    public static void removeAllModifiers(Player player) {
        AttributeModifierManager manager = AttributeModifierManager.getInstance();
        for (String identifier : manager.getModifiers(ModifierType.POSITIVE).keySet()) {
            removeUniqueModifier(player, manager.get(ModifierType.POSITIVE, identifier));
        }
        for (String identifier : manager.getModifiers(ModifierType.NEGATIVE).keySet()) {
            removeUniqueModifier(player, manager.get(ModifierType.NEGATIVE, identifier));
        }
        manager.removeHealthLevelModifier(player);
    }

    public static void addDTModifier(XRPGPlayer xrpgPlayer, String sourceName, double amount) {
        if (xrpgPlayer.dmgTakenMultipliers.containsKey(sourceName))
            xrpgPlayer.dmgTakenMultipliers.put(sourceName, amount);
    }

    @SuppressWarnings("all")
    public static void removeDTModifier(XRPGPlayer xrpgPlayer, String sourceName) {
        if (xrpgPlayer.dmgTakenMultipliers.containsKey(sourceName))
            xrpgPlayer.dmgTakenMultipliers.remove(sourceName);
    }

    public static RayTraceResult rayTrace(LivingEntity caster, double range, FluidCollisionMode collisionMode) {
        return caster.getLocation().getWorld().rayTrace(caster.getEyeLocation(), caster.getEyeLocation().getDirection(), range, collisionMode, true, 0.3, p -> p instanceof LivingEntity && p != caster);

    }

    public static void addPotionEffects(List<Player> players, List<PotionEffect> effects) {

    }

    public static int getLastAvailableInventorySlot(Inventory inventory) {
        for (int i = inventory.getSize() - 1; i > 2; i--) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }

    public static ItemStack buildItemStack(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (lore == null || !lore.isEmpty()){
                meta.setLore(lore);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }



}
