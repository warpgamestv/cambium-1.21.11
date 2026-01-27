package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {

                // Example: ALL Logs -> Charcoal
                ResourceKey<Recipe<?>> customKey = ResourceKey.create(
                        Registries.RECIPE,
                        Identifier.fromNamespaceAndPath("cambium", "solar_digesting/charcoal_from_logs")
                );

                SolarDigesterRecipeBuilder.digester(this.tag(ItemTags.LOGS), Items.CHARCOAL, 1)
                        .byproduct(ModItems.ORGANIC_ASH, 1, 0.50f)
                        .time(200)
                        .xp(1.0f)
                        .unlockedBy("has_logs", this.has(ItemTags.LOGS))
                        .save(this.output, customKey);

                //Example: Sand -> Glass
                ResourceKey<Recipe<?>> glassKey = ResourceKey.create(
                        Registries.RECIPE,
                        Identifier.fromNamespaceAndPath("cambium", "solar_digesting/glass_from_sand")
                );

                SolarDigesterRecipeBuilder.digester(Items.SAND, Items.GLASS, 1)
                        .time(100)        // Faster than furnace
                        .xp(0.1f)
                        .requiresLens()   // <--- TEST: This triggers the JEI item display
                        .unlockedBy("has_sand", this.has(Items.SAND))
                        .save(this.output, glassKey);
            }
        };
    }

    @Override
    public String getName() {
        return "Cambium Recipes";
    }
}