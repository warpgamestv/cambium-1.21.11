package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItemGroups;
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

        // --- Blocks ---
        builder.add(ModBlocks.SOLAR_DIGESTER, "Solar Digester");
        builder.add(ModBlocks.MINERAL_SOIL, "Mineral Soil");
        builder.add(ModBlocks.LIVING_LOG, "Living Log");

        // --- Items ---
        // builder.add(ModItems.LENS, "Focusing Lens");
        // builder.add(ModItems.BYPRODUCT, "Organic Ash"); // Or whatever name you want

        // --- UI / Messages ---
        builder.add("container.solar_digester", "Solar Digester");
        builder.add("rei.cambium.solar_digesting", "Solar Digesting");
    }
}