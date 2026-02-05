package com.warpgames.cambium.datagen;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
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
                Ingredient ashTemplate = Ingredient.of(ModItems.ORGANIC_ASH);
                Ingredient baseSapling = Ingredient.of(Items.OAK_SAPLING);

                // 1. Get the Registry Lookup (This is the key to fixing the error)
                var itemRegistry = registries.lookupOrThrow(Registries.ITEM);

                for (ResourceTree tree : TreeRegistry.TREES) {

                    Ingredient inputIngredient = null;

                    try {
                        // STRATEGY 1: Use Tag via Registry Lookup
                        if (tree.getIngredientTag() != null) {
                            // This returns exactly 'HolderSet.Named<Item>', which Ingredient.of() accepts.
                            var tagSet = itemRegistry.getOrThrow(tree.getIngredientTag());
                            inputIngredient = Ingredient.of(tagSet);
                        }
                        // STRATEGY 2: Use Item (Fallback)
                        else {
                            Item resourceItem = tree.getItem();
                            if (resourceItem == Items.AIR && !tree.getRawItemId().isEmpty()) {
                                Identifier id = Identifier.tryParse(tree.getRawItemId());
                                if (id != null) resourceItem = BuiltInRegistries.ITEM.getValue(id);
                            }
                            if (resourceItem != Items.AIR) {
                                inputIngredient = Ingredient.of(resourceItem);
                            }
                        }
                    } catch (Exception e) {
                        Cambium.LOGGER.warn("Skipping Splicing Recipe for '{}' - Tag/Item is invalid.", tree.getName());
                        continue;
                    }

                    if (inputIngredient == null) {
                        Cambium.LOGGER.warn("Skipping Splicing Recipe for '{}' - No valid ingredient resolved.", tree.getName());
                        continue;
                    }

                    SmithingTransformRecipeBuilder.smithing(
                                    ashTemplate,
                                    baseSapling,
                                    inputIngredient,
                                    RecipeCategory.MISC,
                                    tree.getSapling().asItem()
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