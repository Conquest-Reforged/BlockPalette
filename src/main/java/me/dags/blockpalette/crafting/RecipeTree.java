package me.dags.blockpalette.crafting;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import me.dags.blockpalette.palette.Palette;
import me.dags.blockpalette.palette.PaletteItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class RecipeTree {

    private Map<ItemState, Collection<Recipe>> recipes = Collections.emptyMap();

    public void buildRecipeTree() {
        HashMultimap<ItemState, Recipe> tree = HashMultimap.create();

        outer:
        for (IRecipe recipe : CraftingManager.REGISTRY) {
            ItemState input = ItemState.EMPTY;
            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack stack : ingredient.getMatchingStacks()) {
                    if (input.isPresent() && !input.matches(stack)) {
                        continue outer; // recipe requires multiple ingredients
                    }
                    input = input.add(stack);
                }
            }
            ItemState output = ItemState.of(recipe.getRecipeOutput());
            tree.put(input, new Recipe(input, output));
        }

        recipes = ImmutableMap.copyOf(tree.asMap());
    }

    public Collection<Recipe> getAllRecipes(ItemStack stack) {
        return recipes.getOrDefault(ItemState.of(stack), Collections.emptyList());
    }

    public Collection<Recipe> getRecipes(ItemStack stack) {
        Collection<Recipe> results = new LinkedList<>();
        for (Recipe recipe : getAllRecipes(stack)) {
            if (recipe.test(stack)) {
                results.add(recipe);
            }
        }
        return results;
    }

    public Palette getPalette(ItemStack stack) {
        if (stack == null || Block.getBlockFromItem(stack.getItem()) == Blocks.AIR) {
            return Palette.EMPTY;
        }

        PaletteItem center = PaletteItem.of(stack);
        List<PaletteItem> options = new LinkedList<>();
        Collection<Recipe> recipes = getRecipes(stack);
        for (Recipe recipe : recipes) {
            PaletteItem item = PaletteItem.of(recipe.getOutput().getStack());
            options.add(item);
        }

        return Palette.craftingPalette(center, options);
    }
}
