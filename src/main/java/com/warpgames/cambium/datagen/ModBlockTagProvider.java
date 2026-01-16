package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        // 1. Register Logs
        // This stops the leaves from decaying when touching them
        valueLookupBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.LIVING_LOG)
        // Add stripped versions or wood blocks here if you have them:
        // .add(ModBlocks.STRIPPED_LIVING_LOG)
        // .add(ModBlocks.LIVING_WOOD)
        ;

        // 2. Register Leaves
        // This makes them behave like foliage (transparent, decayable)
        valueLookupBuilder(BlockTags.LEAVES)
                .add(ModBlocks.LIVING_LEAVES);
    }
}