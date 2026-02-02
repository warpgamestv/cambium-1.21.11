package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

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
                //Solar Digester Recipes
                SolarDigesterRecipeBuilder.digester(this.tag(ModTags.Items.DIGESTABLE), ModItems.ORGANIC_ASH, 1)
                        .byproduct(ModItems.ORGANIC_ASH, 1, 0.25f)
                        .time(150)
                        .xp(1.0f)
                        .unlockedBy("has_logs", this.has(ItemTags.LOGS))
                        .save(this.output);

                //Solar Concentrator Recipes
                SolarConcentratorRecipeBuilder.concentrator(Items.SAND, Items.GLASS, 1)
                        .time(75)
                        .xp(0.1f)
                        .requiresLens()
                        .unlockedBy("has_sand", this.has(Items.SAND))
                        .save(this.output);

                SolarConcentratorRecipeBuilder.concentrator(ModItems.BIOCOMPOSITE_PASTE, ModItems.BIOPOLYMER, 1)
                                .time(75)
                                .xp(0.1f)
                                .requiresLens()
                                .unlockedBy("has_biocomposite_paste", this.has(ModItems.BIOCOMPOSITE_PASTE))
                                .save(this.output);

                //Crafting Table Recipes
                shaped(RecipeCategory.MISC, ModBlocks.SOLAR_DIGESTER)
                        .pattern("GGG")
                        .pattern("BCB")
                        .pattern("LLL")
                        .define('G', Items.GLASS)
                        .define('L', ItemTags.LOGS)
                        .define('C', Items.COMPOSTER)
                        .define('B', Items.COPPER_INGOT)
                        .unlockedBy("has_composter", has(Items.COMPOSTER))
                        .save(this.output);

                shaped(RecipeCategory.MISC, ModBlocks.SOLAR_CONCENTRATOR)
                        .pattern("GGG")
                        .pattern("PFP")
                        .pattern("PPP")
                        .define('G', Items.GLASS)
                        .define('F', Items.FURNACE)
                        .define('P', ModItems.BIOPOLYMER_CASING)
                        .unlockedBy("has_furnace", has(Items.FURNACE))
                        .save(this.output);

                shaped(RecipeCategory.MISC, ModItems.SOLAR_LENS)
                        .pattern(" G ")
                        .pattern("GPG")
                        .pattern(" G ")
                        .define('G', Items.GOLD_INGOT)
                        .define('P', Items.GLASS_PANE)
                        .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                        .save(this.output);

                shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MINERAL_SOIL)
                        .pattern("AAA")
                        .pattern("ADA")
                        .pattern("AAA")
                        .define('A', ModItems.ORGANIC_ASH)
                        .define('D', Items.DIRT)
                        .unlockedBy("has_ash", has(ModItems.ORGANIC_ASH))
                        .save(this.output);

                shapeless(RecipeCategory.MISC, ModItems.BIOCOMPOSITE_PASTE, 2)
                        .requires(ModItems.ORGANIC_ASH)
                        .requires(Items.CLAY_BALL)
                        .unlockedBy("has_ash", has(ModItems.ORGANIC_ASH))
                        .save(this.output);

                SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.BIOCOMPOSITE_PASTE), RecipeCategory.MISC, ModItems.BIOPOLYMER, 0.35F, 200)
                        .unlockedBy("has_biocomposite_paste", this.has(ModItems.BIOCOMPOSITE_PASTE))
                        .save(this.output);

                shaped(RecipeCategory.MISC, ModItems.BIOPOLYMER_CASING)
                        .pattern("P P")
                        .pattern("LCL")
                        .pattern("P P")
                        .define('P', ModItems.BIOPOLYMER)
                        .define('L', ModItems.SOLAR_LENS)
                        .define('C', Items.COPPER_INGOT)
                        .unlockedBy("has_polymer", has(ModItems.BIOPOLYMER))
                        .save(this.output);
            }
        };
    }

    @Override
    public String getName() {
        return "Cambium Recipes";
    }
}