package com.warpgames.cambium.datagen;

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
                // Example 1: Oak Log -> Charcoal
                SolarDigesterRecipeBuilder.digester(Items.OAK_LOG, Items.CHARCOAL, 1)
                        .byproduct(Items.STICK, 2, 0.50f)
                        .time(100)
                        .xp(0.5f)
                        .unlockedBy("has_oak_log", this.has(Items.OAK_LOG))
                        .save(this.output);

                // Example 2: ALL Logs -> Charcoal
                ResourceKey<Recipe<?>> customKey = ResourceKey.create(
                        Registries.RECIPE,
                        Identifier.fromNamespaceAndPath("cambium", "solar_digesting/charcoal_from_logs")
                );

                // FIX: Use 'this.tag(ItemTags.LOGS)' instead of just 'ItemTags.LOGS'
                // This uses the internal datagen registry lookup, which prevents the crash.
                SolarDigesterRecipeBuilder.digester(this.tag(ItemTags.LOGS), Items.CHARCOAL, 1)
                        .byproduct(Items.DEAD_BUSH, 1, 0.10f)
                        .time(200)
                        .xp(1.0f)
                        .unlockedBy("has_logs", this.has(ItemTags.LOGS))
                        .save(this.output, customKey);
            }
        };
    }

    @Override
    public String getName() {
        return "Cambium Recipes";
    }
}