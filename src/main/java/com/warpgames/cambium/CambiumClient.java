package com.warpgames.cambium;

import com.warpgames.cambium.client.screen.SolarDigesterScreen;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModScreenHandlers;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;

public class CambiumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        Cambium.LOGGER.info("Client Init: Found " + TreeRegistry.TREES.size() + " trees to color.");

        for (ResourceTree tree : TreeRegistry.TREES) {
            Block leaves = tree.getLeaves();

            Cambium.LOGGER.info("Registering color for: " + tree.getName() + " | Color: " + tree.getColor());

            if (leaves != null) {
                // Block Colors (World)
                ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    return tree.getColor();
                }, leaves);

                // 2. TRANSPARENCY (Keep this!)
                BlockRenderLayerMap.putBlock(leaves, ChunkSectionLayer.CUTOUT);
            }

            if (tree.getFruit() != null) {
                BlockRenderLayerMap.putBlock(tree.getFruit(), ChunkSectionLayer.CUTOUT);
            }
        }
        MenuScreens.register(ModScreenHandlers.SOLAR_DIGESTER_MENU, SolarDigesterScreen::new);
    }
}