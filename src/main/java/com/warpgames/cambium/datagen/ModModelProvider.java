package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

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
        for (ResourceTree tree : TreeRegistry.TREES) {
            Block fruit = tree.getFruit();

            // Construct the texture path dynamically
            // e.g., "minecraft:block/raw_gold_block"
            Identifier textureId = Identifier.fromNamespaceAndPath("minecraft", "block/raw_" + tree.getName() + "_block");

            // Create a model using that texture
            // (You might need a custom method to generate a "Cross" or "Cube" model with this texture)
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        // We don't need this yet since the block model usually handles the item model for cubes
    }
}