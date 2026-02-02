package com.warpgames.cambium.compat.jei;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.recipe.ModRecipes;
import com.warpgames.cambium.recipe.SolarConcentratorRecipe;
import com.warpgames.cambium.recipe.SolarDigesterRecipe;
import com.warpgames.cambium.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CambiumJEIPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // 1. Register Digester Category
        registration.addRecipeCategories(new SolarDigesterCategory(registration.getJeiHelpers().getGuiHelper()));

        // 2. Register Concentrator Category
        registration.addRecipeCategories(new SolarConcentratorCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<SolarDigesterRecipe> solarRecipes = new ArrayList<>();
        List<SolarConcentratorRecipe> concentratorRecipes = new ArrayList<>();

        RecipeManager serverRecipeManager = null;

        if (Minecraft.getInstance().getSingleplayerServer() != null) {
            serverRecipeManager = Minecraft.getInstance().getSingleplayerServer().getRecipeManager();
        }
        if (serverRecipeManager != null) {
            for (RecipeHolder<?> holder : serverRecipeManager.getRecipes()){

                // --- LOAD DIGESTER RECIPES ---
                if (holder.value().getType() == ModRecipes.SOLAR_DIGESTER_TYPE) {
                    if (holder.value() instanceof SolarDigesterRecipe recipe) {
                        solarRecipes.add(recipe);
                    }
                }
                // --- LOAD CONCENTRATOR RECIPES ---
                if (holder.value().getType() == ModRecipes.SOLAR_CONCENTRATOR_TYPE) {
                    if (holder.value() instanceof SolarConcentratorRecipe recipe) {
                        concentratorRecipes.add(recipe);
                    }
                }
            }
        }

        // Add both sets of recipes
        registration.addRecipes(SolarDigesterCategory.RECIPE_TYPE, solarRecipes);
        registration.addRecipes(SolarConcentratorCategory.RECIPE_TYPE, concentratorRecipes);
    }

    @SuppressWarnings("removal")
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SOLAR_DIGESTER), SolarDigesterCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SOLAR_CONCENTRATOR), SolarConcentratorCategory.RECIPE_TYPE);
    }
}