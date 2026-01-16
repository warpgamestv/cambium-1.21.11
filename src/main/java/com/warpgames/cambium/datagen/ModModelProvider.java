package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModBlocks;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // This line tells the generator: "Make a simple cube model for ROOT_BLOCK"
        blockStateModelGenerator.createTrivialCube(ModBlocks.ROOT_BLOCK);
        blockStateModelGenerator.woodProvider(ModBlocks.LIVING_LOG)
                .logWithHorizontal(ModBlocks.LIVING_LOG);
        blockStateModelGenerator.createTrivialCube(ModBlocks.LIVING_LEAVES);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        // We don't need this yet since the block model usually handles the item model for cubes
    }
}