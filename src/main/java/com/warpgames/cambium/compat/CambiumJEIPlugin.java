package com.warpgames.cambium.compat;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.recipe.ModRecipes;
import com.warpgames.cambium.recipe.SolarDigesterRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class CambiumJEIPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SolarDigesterRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 1. Safety Check
        if (Minecraft.getInstance().level == null) return;

        // 2. Get the RecipeManager
        // CORRECTION: Using 'recipeAccess()' based on your screenshot/mappings
        RecipeManager recipeManager = (RecipeManager) Minecraft.getInstance().level.recipeAccess();

        // 3. Get Recipes
        // CORRECTION: Using 'getRecipes()' based on your RecipeManager.java file (Line 146)
        List<SolarDigesterRecipe> recipes = recipeManager.getRecipes()
                .stream()
                // Filter: Check if the recipe type matches ours
                .filter(holder -> holder.value().getType() == ModRecipes.SOLAR_DIGESTER_TYPE)
                // Map: Unwrap the holder to get the actual recipe class
                .map(holder -> (SolarDigesterRecipe) holder.value())
                .toList();

        // 4. Register
        registration.addRecipes(SolarDigesterRecipeCategory.RECIPE_TYPE, recipes);
    }
}