package me.xepos.rpg.configuration;

import me.xepos.rpg.XRPG;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CraftLoader {
    private final XRPG plugin;
    private final File craftingConfig;

    public CraftLoader(XRPG plugin) {
        this.plugin = plugin;
        plugin.saveResource("recipes.yml", false);

        this.craftingConfig = new File(plugin.getDataFolder(), "recipes.yml");

    }

    public void initCustomRecipes() {
        ConfigurationSection recipeSection = YamlConfiguration.loadConfiguration(craftingConfig).getConfigurationSection("recipes");

        if (recipeSection == null) {
            Bukkit.getLogger().severe("Config does not contain a section for recipes!");
            Bukkit.getLogger().severe("Custom recipes will not be able to load!");
            return;
        }

        for (String key : recipeSection.getKeys(false)) {
            //Retrieving all the data for the recipe
            String name = recipeSection.getString(key + ".item-name", "Undefined");
            final boolean shaped = recipeSection.getBoolean(key + ".shaped-recipe");

            String itemName = recipeSection.getString(key + ".item", "STICK").toUpperCase();
            ItemStack item;
            try {
                item = new ItemStack(Material.valueOf(itemName));
            } catch (IllegalArgumentException ex) {
                Bukkit.getLogger().info("Could not find item: \"" + itemName + "\"! Using STICK instead");
                item = new ItemStack(Material.STICK);
            }
            String tag = recipeSection.getString(key + ".tag");

            if (tag == null || tag.isEmpty()) {
                Bukkit.getLogger().severe("Could not find mob id for " + name + "!");
                Bukkit.getLogger().severe("Skipping this recipe...");
                continue;
            }

            //Modifying item meta
            ItemMeta iconMeta = item.getItemMeta();
            iconMeta.setDisplayName(name);
            //iconMeta.getPersistentDataContainer().set(plugin.getTagKey(), PersistentDataType.STRING, tag);
            item.setItemMeta(iconMeta);

            //Making recipe object and its id
            NamespacedKey craftingKey = new NamespacedKey(plugin, UUID.randomUUID().toString());

            Recipe recipe;
            Bukkit.getLogger().info("Shaped recipe: " + shaped);
            if (shaped) {
                List<String> shape = recipeSection.getStringList(key + ".recipe-shape");
                ShapedRecipe shapedRecipe = new ShapedRecipe(craftingKey, item);

                //Not proud of this but it will work
                switch (shape.size()) {
                    case 0:
                        Bukkit.getLogger().severe("Recipe shape is incorrect!");
                        Bukkit.getLogger().severe("Skipping this recipe...");
                        continue;
                    case 2:
                        shapedRecipe.shape(shape.get(0), shape.get(1));
                        break;
                    case 1:
                        shapedRecipe.shape(shape.get(0));
                        break;
                    default:
                        shapedRecipe.shape(shape.get(0), shape.get(1), shape.get(2));
                        break;
                }

                //linking ingredients to the shape
                setShapedIngredients(key, shapedRecipe, recipeSection);

                recipe = shapedRecipe;
            } else {
                ShapelessRecipe shapelessRecipe = new ShapelessRecipe(craftingKey, item);

                setShapelessIngredients(key, shapelessRecipe, recipeSection);

                recipe = shapelessRecipe;
            }

            //Adding the recipe
            if (Bukkit.addRecipe(recipe)) {
                Bukkit.getLogger().info("Loaded recipe: " + name);
            } else {
                Bukkit.getLogger().warning("Failed to load recipe: " + name);
            }
        }

    }

    private void setShapedIngredients(String key, ShapedRecipe recipe, ConfigurationSection recipeSection) {
        List<String> ingredients = recipeSection.getStringList(key + ".ingredients");

        for (String ingredient : ingredients) {
            String[] strings = ingredient.split(":");
            strings[0] = strings[0].trim().toUpperCase();
            strings[1] = strings[1].trim().toUpperCase();

            Material material;
            try {
                material = Material.valueOf(strings[1]);
            } catch (IllegalArgumentException ex) {
                Bukkit.getLogger().severe("Could not find item: \"" + strings[1] + "\"!");
                continue;
            }

            recipe.setIngredient(strings[0].toCharArray()[0], material);
        }
    }

    private void setShapelessIngredients(String key, ShapelessRecipe recipe, ConfigurationSection recipeSection) {
        List<String> ingredients = recipeSection.getStringList(key + ".ingredients");

        for (String ingredient : ingredients) {
            String[] strings = ingredient.split(":");
            strings[0] = strings[0].trim().toUpperCase();

            if (strings.length == 2) {
                strings[1] = strings[1].trim().toUpperCase();
            }

            if (StringUtils.isNumeric(strings[0])) {
                Material material;
                try {
                    material = Material.valueOf(strings[1]);
                } catch (IllegalArgumentException ex) {
                    Bukkit.getLogger().severe("Could not find item: \"" + strings[1] + "\"!");
                    continue;
                }

                recipe.addIngredient(Integer.parseInt(strings[0]), material);
            } else {

                Material material;
                try {
                    material = Material.valueOf(strings[0]);
                } catch (IllegalArgumentException ex) {
                    Bukkit.getLogger().severe("Could not find item: \"" + strings[0] + "\"!");
                    continue;
                }

                recipe.addIngredient(1, material);
            }
        }

    }
}
