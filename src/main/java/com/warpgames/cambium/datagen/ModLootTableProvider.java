package com.warpgames.cambium.datagen;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
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
        // 1. Static Blocks (Standard Drops)
        dropSelf(ModBlocks.ROOT_BLOCK);
        dropSelf(ModBlocks.MINERAL_SOIL);
        dropSelf(ModBlocks.LIVING_LOG);

        // 2. Solar Digester (Drops itself + the items inside)
        // We use 'createNameableBlockEntityTable' so it keeps its custom name if renamed
        add(ModBlocks.SOLAR_DIGESTER, createNameableBlockEntityTable(ModBlocks.SOLAR_DIGESTER));

        // 3. Dynamic Resource Tree Drops
        for (ResourceTree tree : TreeRegistry.TREES) {
            if (tree.getLeaves() != null) {
                // Leaves drop themselves
                dropSelf(tree.getLeaves());
            }
            if (tree.getFruit() != null) {
                // Fruit drops itself
                dropSelf(tree.getFruit());
            }

            // Note: Saplings are usually handled by the block class itself
            // or registered separately if you have a reference to them.
        }
    }
}