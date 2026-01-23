package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
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
        // NOTE: In your version of Fabric, the method to get a builder
        // that accepts BLOCKS (not Keys) is 'getOrCreateTagBuilder' or 'tag'.
        // However, your source code shows 'valueLookupBuilder' is the special Fabric one.
        // Let's try the standard 'getOrCreateTagBuilder' which comes from the Minecraft parent class.

        // 1. Logs
        valueLookupBuilder(BlockTags.LOGS)
                .add(ModBlocks.LIVING_LOG)
                .add(ModBlocks.ROOT_BLOCK);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.LIVING_LOG)
                .add(ModBlocks.ROOT_BLOCK);

        // 2. Soil
        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MINERAL_SOIL);

        // 3. Leaves
        var leavesTag = valueLookupBuilder(BlockTags.LEAVES);
        var hoeTag = valueLookupBuilder(BlockTags.MINEABLE_WITH_HOE);

        for (ResourceTree tree : TreeRegistry.TREES) {
            if (tree.getLeaves() != null) {
                leavesTag.add(tree.getLeaves());
                hoeTag.add(tree.getLeaves());
            }
        }
    }
}