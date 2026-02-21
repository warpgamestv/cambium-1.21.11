package com.warpgames.cambium;

import com.warpgames.cambium.client.renderer.PhloemDuctRenderer;
import com.warpgames.cambium.client.screen.SolarConcentratorScreen;
import com.warpgames.cambium.client.screen.SolarDigesterScreen;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModScreenHandlers;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class CambiumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Cambium.LOGGER.info("Client Init: Setting up Block Colors and Render Layers.");

        for (ResourceTree tree : TreeRegistry.TREES) {
            Block leaves = tree.getLeaves();
            Block sapling = tree.getSapling();
            Block fruit = tree.getFruit();

            // --- A. LEAVES ---
            if (leaves != null) {
                // Block Colors (World)
                ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> tree.getColor(), leaves);

                // Transparency (Using your preferred method)
                BlockRenderLayerMap.putBlock(leaves, ChunkSectionLayer.CUTOUT);
            }

            // --- B. SAPLINGS ---
            if (sapling != null) {
                // Block Colors (World)
                ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    return tintIndex == 0 ? tree.getColor() : -1;
                }, sapling);

                // Transparency (Using your preferred method)
                BlockRenderLayerMap.putBlock(sapling, ChunkSectionLayer.CUTOUT);
            }

            // --- C. FRUIT ---
            if (fruit != null) {
                ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> tree.getColor(), fruit);
            }
        }

        // Static Blocks
        BlockRenderLayerMap.putBlock(ModBlocks.ROOT_BLOCK, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.MINERAL_SOIL, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.PHLOEM_DUCT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.PHLOEM_DUCT, ChunkSectionLayer.TRANSLUCENT);
        BlockEntityRendererRegistry.register(ModBlockEntities.PHLOEM_DUCT, PhloemDuctRenderer::new);

        MenuScreens.register(ModScreenHandlers.SOLAR_DIGESTER_MENU, SolarDigesterScreen::new);
        MenuScreens.register(ModScreenHandlers.SOLAR_CONCENTRATOR_SCREEN_HANDLER, SolarConcentratorScreen::new);
        MenuScreens.register(ModScreenHandlers.MYCELIAL_NODE_MENU,
                com.warpgames.cambium.screen.MycelialNodeScreen::new);

        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
                com.warpgames.cambium.network.MycelialNetworkSyncPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        if (context.client().screen instanceof com.warpgames.cambium.screen.MycelialNodeScreen screen) {
                            screen.setNetworkItems(payload.items());
                        }
                    });
                });
    }
}