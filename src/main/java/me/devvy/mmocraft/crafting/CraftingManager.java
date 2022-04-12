package me.devvy.mmocraft.crafting;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.items.ItemData;
import me.devvy.mmocraft.items.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class CraftingManager implements Listener {

    private final List<Recipe> registeredRecipes = new ArrayList<>();

    public CraftingManager() {

//        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
//
//
//        registerEnchDiamond();
//        registerEnchDiamondBlock();
//        registerZombieSword();
//        registerDiamondSingularity();

    }

    public void registerRecipe(Recipe recipe) {
        MMOCraft.getInstance().getServer().addRecipe(recipe);
        registeredRecipes.add(recipe);
    }

    // Given a sequence of items, let these items be able to craft into each other in compressed form
    // ex. diamond -> block of diamond -> enchanted diamond -> block of enchanted diamond -> diamond singularity
    public void registerCompressedCraftingChain(ItemStack...itemStacks) {

        // If we were passed in less than 2 items, the developer for sure screwed up
        if (itemStacks.length < 2)
            throw new IllegalArgumentException("More than 1 itemstack is required to setup a recipe chain!");

        // Iterate through two items at a time, but do not run the loop on the last item
        for (int i = 0; i < itemStacks.length-1; i++) {

            // The item to be used to craft next
            ItemStack current = itemStacks[i];
            // The item to be crafted from current
            ItemStack next = itemStacks[i+1];

            // First make the recipe that makes next
            ShapelessRecipe newRecipe = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "recipe-" + ChatColor.stripColor(current.getItemMeta().displayName().toString())), current);
            newRecipe.addIngredient(9, next);

            // Now make the recipe that goes backwards
            ShapelessRecipe backwards = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "ench_diamond_reverse_recipe"), new ItemStack(Material.DIAMOND_BLOCK, 9));
            backwards.addIngredient(ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND"));

//            registerRecipe(enchDiamRecipe);
            registerRecipe(backwards);

        }

    }

    public void registerEnchDiamond() {

        ShapelessRecipe enchDiamRecipe = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "ench_diamond_recipe"), ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND"));
        enchDiamRecipe.addIngredient(9, Material.DIAMOND_BLOCK);

        ShapelessRecipe backwards = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "ench_diamond_reverse_recipe"), new ItemStack(Material.DIAMOND_BLOCK, 9));
        backwards.addIngredient(ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND"));

        registerRecipe(enchDiamRecipe);
        registerRecipe(backwards);

    }

    public void registerZombieSword() {
        ShapedRecipe sword = new ShapedRecipe(new NamespacedKey(MMOCraft.getInstance(), "zombie_sword_recipe"), ItemManager.getInstance().generateCustomItem("ZOMBIE_KING_SWORD"));
        sword.shape(" F ", " F ", " S ");
        sword.setIngredient('F', ItemManager.getInstance().generateCustomItem("PREMIUM_FLESH"));
        sword.setIngredient('S', Material.STICK);

        registerRecipe(sword);

    }

    public void registerEnchDiamondBlock() {

        ShapelessRecipe enchDiamRecipe = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "ench_diamond_block_recipe"), ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND_BLOCK"));
        enchDiamRecipe.addIngredient(9, ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND"));

        ShapelessRecipe backwards = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "ench_diamond_block_reverse_recipe"), ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND", 9));
        backwards.addIngredient(ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND_BLOCK"));

        registerRecipe(enchDiamRecipe);
        registerRecipe(backwards);

    }

    public void registerDiamondSingularity() {
        ShapelessRecipe dsing = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "diamond_singularity_recipe"), ItemManager.getInstance().generateCustomItem("DIAMOND_SINGULARITY"));
        dsing.addIngredient(9, ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND_BLOCK"));

        ShapelessRecipe backwards = new ShapelessRecipe(new NamespacedKey(MMOCraft.getInstance(), "diamond_singularity_reverse_recipe"), ItemManager.getInstance().generateCustomItem("ENCHANTED_DIAMOND_BLOCK", 9));
        backwards.addIngredient(ItemManager.getInstance().generateCustomItem("DIAMOND_SINGULARITY"));

        registerRecipe(dsing);
        registerRecipe(backwards);

        ItemData id = ItemManager.getInstance().getCustomItemData(ItemManager.getInstance().generateCustomItem("DIAMOND_SINGULARITY"));
//        blacklistedVanillaItems.put("DIAMOND_SINGULARITY", id);

    }

    // Returns whether the recipe is vanilla or custom
    public boolean isVanilla(Recipe recipe) {
        return !registeredRecipes.contains(recipe);
    }

    // Cancel crafting events for items that shouldn't be allowed
    @EventHandler
    public void onAttemptCraft(PrepareItemCraftEvent event) {



    }




}
