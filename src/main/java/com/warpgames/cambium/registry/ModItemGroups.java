package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.content.ResourceTree;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.warpgames.cambium.Cambium.MOD_ID;

public class ModItemGroups {

//Tab 1: General Cambium
    public static final CreativeModeTab CAMBIUM_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(MOD_ID,"cambium_tab"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.SOLAR_DIGESTER)) // The icon on the tab
                    .title(Component.translatable("itemGroup.cambium")) // The hover text
                    .displayItems((context, entries) -> {
                        // --- Add your items here ---
                        entries.accept(ModBlocks.SOLAR_DIGESTER);
                        entries.accept(ModBlocks.ROOT_BLOCK);
                        entries.accept(ModBlocks.MINERAL_SOIL);
                        entries.accept(ModBlocks.LIVING_LOG);
                        entries.accept(ModItems.FOCUSING_LENS);
                        entries.accept(ModItems.ORGANIC_ASH);
                    })
                    .build());

    // --- TAB 2: RESOURCE TREES (Leaves, Fruit, Saplings) ---
    public static final CreativeModeTab RESOURCE_TREES_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(MOD_ID, "resource_trees_tab"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(Items.OAK_SAPLING)) // You can change this to a custom icon later
                    .title(Component.translatable("itemGroup.cambium_trees"))
                    .displayItems((context, entries) -> {
                        // Dynamically add all tree-related blocks from your registry
                        for (ResourceTree tree : TreeRegistry.TREES) {
                            if (tree.getLeaves() != null) entries.accept(tree.getLeaves());
                            if (tree.getFruit() != null) entries.accept(tree.getFruit());

                            // To add saplings, we retrieve them by their identifier
                            Identifier saplingId = Identifier.fromNamespaceAndPath(MOD_ID, tree.getName() + "_sapling");
                            BuiltInRegistries.ITEM.getOptional(saplingId).ifPresent(entries::accept);
                        }
                    })
                    .build());

    public static void registerItemGroups() {
        Cambium.LOGGER.info("Registering Item Groups for " + MOD_ID);
    }
}