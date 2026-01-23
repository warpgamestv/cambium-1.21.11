package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // 1. Simple Blocks (Drop themselves)
        // dropSelf(ModBlocks.MINERAL_SOIL);

        // 2. Machines (Drop themselves + Inventory contents)
        // The 'nameableContainerLootTable' helper handles the "Drop Contents" logic for us!
        add(ModBlocks.SOLAR_DIGESTER, nameableContainerLootTable(ModBlocks.SOLAR_DIGESTER));
    }
}