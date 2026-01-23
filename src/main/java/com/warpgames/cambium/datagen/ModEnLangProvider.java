package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItemGroups;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModEnLangProvider extends FabricLanguageProvider {

    public ModEnLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // --- Item Groups ---
        builder.add("itemGroup.cambium", "Cambium");
        builder.add("itemGroup.cambium_trees", "Cambium Resource Trees");

        // --- Blocks ---
        builder.add(ModBlocks.SOLAR_DIGESTER, "Solar Digester");
        builder.add(ModBlocks.MINERAL_SOIL, "Mineral Soil");
        builder.add(ModBlocks.LIVING_LOG, "Living Log");
        builder.add(ModBlocks.ROOT_BLOCK, "Root Block");

        // --- Items ---
        builder.add(ModItems.FOCUSING_LENS, "Focusing Lens");
        builder.add(ModItems.ORGANIC_ASH, "Organic Ash");

        // --- Dynamic Resource Tree Support ---
        // This loop goes through every tree in your registry and names its parts
        for (ResourceTree tree : TreeRegistry.TREES) {
            String rawName = tree.getName();
            String capitalizedName = rawName.substring(0, 1).toUpperCase() + rawName.substring(1).toLowerCase();

            // 1. Leaves
            // We register both block and item keys to be safe
            builder.add("block.cambium." + rawName + "_leaves", capitalizedName + " Leaves");
            builder.add("item.cambium." + rawName + "_leaves", capitalizedName + " Leaves");

            // 2. Fruit
            builder.add("block.cambium." + rawName + "_fruit", capitalizedName + " Fruit");
            builder.add("item.cambium." + rawName + "_fruit", capitalizedName + " Fruit");

            // 3. Saplings
            builder.add("block.cambium." + rawName + "_sapling", capitalizedName + " Sapling");
            builder.add("item.cambium." + rawName + "_sapling", capitalizedName + " Sapling");
        }

        // --- UI / Messages ---
        builder.add("container.solar_digester", "Solar Digester");
        builder.add("jei.cambium.solar_digesting", "Solar Digesting");
    }
}