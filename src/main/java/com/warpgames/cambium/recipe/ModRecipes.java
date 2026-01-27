package com.warpgames.cambium.recipe;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {

    // The Serializer
    public static final RecipeSerializer<SolarDigesterRecipe> SOLAR_DIGESTER_SERIALIZER =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digesting"), new SolarDigesterRecipe.Serializer());

    // The Type
    public static final RecipeType<SolarDigesterRecipe> SOLAR_DIGESTER_TYPE =
            Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digesting"), new RecipeType<SolarDigesterRecipe>() {
                @Override
                public String toString() {
                    return "solar_digesting";
                }
            });

    public static void registerRecipes() {
        Cambium.LOGGER.info("Registering Custom Recipes for " + Cambium.MOD_ID);
    }
}