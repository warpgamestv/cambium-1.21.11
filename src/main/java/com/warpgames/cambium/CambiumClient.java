package com.warpgames.cambium;

import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.level.block.Block;

public class CambiumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1. Loop through all trees
        for (ResourceTree tree : TreeRegistry.TREES) {
            Block leavesBlock = tree.getLeaves(); // You might need a getter in ResourceTree
            int color = tree.getColor();

            // 2. Register Block Color
            ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                return color; // Return the hex color (e.g., 0xFFD700)
            }, leavesBlock);

            // 3. Register Item Color (for the inventory item)
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                return color;
            }, leavesBlock);
        }

        // Don't forget to render leaves as "Cutout" so they have transparency!
        // BlockRenderLayerMap.INSTANCE.putBlock(leavesBlock, RenderLayer.getCutout());
    }
}