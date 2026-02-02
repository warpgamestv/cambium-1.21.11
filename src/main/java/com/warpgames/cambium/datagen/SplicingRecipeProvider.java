package com.warpgames.cambium.datagen;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class SplicingRecipeProvider extends FabricRecipeProvider {

    public SplicingRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                // 1. CONSTANTS
                Ingredient ashTemplate = Ingredient.of(ModItems.ORGANIC_ASH);
                Ingredient baseSapling = Ingredient.of(Items.OAK_SAPLING);

                // 2. THE LOOP
                for (ResourceTree tree : TreeRegistry.TREES) {
                    Item resourceItem = tree.getItem();
                    if (resourceItem == Items.AIR && !tree.getRawItemId().isEmpty()) {
                        Identifier id = Identifier.tryParse(tree.getRawItemId());
                        if (id != null) {
                            resourceItem = BuiltInRegistries.ITEM.getValue(id);
                        }
                    }

                    // If it is STILL Air, then the mod is truly missing. Skip it.
                    if (resourceItem == Items.AIR) {
                        Cambium.LOGGER.warn("Skipping Splicing Recipe for '{}' - Item not found.", tree.getName());
                        continue;
                    }
                    SmithingTransformRecipeBuilder.smithing(
                                    ashTemplate,
                                    baseSapling,
                                    Ingredient.of(resourceItem),
                                    RecipeCategory.MISC,
                                    tree.getSapling().asItem() // Use .asItem()
                            )
                            .unlocks("has_ash", this.has(ModItems.ORGANIC_ASH))
                            .save(this.output, "cambium:splicing/" + tree.getName() + "_sapling");
                }
            }
        };
    }

    @Override
    public String getName() {
        return "Cambium Splicing Recipes";
    }
}