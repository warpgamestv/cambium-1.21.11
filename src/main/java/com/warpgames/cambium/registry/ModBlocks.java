package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.*;
import com.warpgames.cambium.content.ResourceTree;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

// NEW IMPORTS FOR DATA COMPONENTS
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;

public class ModBlocks {

    // --- STATIC GENERIC BLOCKS ---
    public static final ResourceKey<Block> ROOT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "root_block"));
    public static final Block ROOT_BLOCK = registerBlock("root_block",
            new RootBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .setId(ROOT_BLOCK_KEY)));

    public static final ResourceKey<Block> SOLAR_DIGESTER_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digester"));
    public static final Block SOLAR_DIGESTER = registerBlock("solar_digester",
            new SolarDigesterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .setId(SOLAR_DIGESTER_KEY)));

    public static final ResourceKey<Block> LIVING_LOG_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "living_log"));
    public static final Block LIVING_LOG = registerBlock("living_log",
            new LivingLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                    .setId(LIVING_LOG_KEY)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()));

    public static final ResourceKey<Block> MINERAL_SOIL_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "mineral_soil"));
    public static final Block MINERAL_SOIL = registerBlock("mineral_soil",
            new MineralSoilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
                    .setId(MINERAL_SOIL_KEY)
                    .strength(1.0f)));

    public static void registerModBlocks() {
        Cambium.LOGGER.info("Registering Mod Blocks for " + Cambium.MOD_ID);
        TreeRegistry.init();

        for (ResourceTree tree : TreeRegistry.TREES) {
            registerTreeBlocks(tree);
        }
    }

    private static void registerTreeBlocks(ResourceTree tree) {
        String leavesName = tree.getName() + "_leaves";
        String fruitName = tree.getName() + "_fruit";
        String saplingName = tree.getName() + "_sapling";

        ResourceKey<Block> leavesKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, leavesName));
        ResourceKey<Block> fruitKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, fruitName));
        ResourceKey<Block> saplingKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, saplingName));

        // 1. Create Blocks
        Block leaves = new LivingLeavesBlock(tree, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                .setId(leavesKey).strength(0.2f).sound(SoundType.GRASS).noOcclusion().ignitedByLava());

        Block fruit = new ResourceFruitBlock(tree, BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA)
                .setId(fruitKey).strength(0.5f).sound(SoundType.GLASS).noOcclusion());

        Block sapling = new ResourceSaplingBlock(tree, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
                .setId(saplingKey).noOcclusion().sound(SoundType.GRASS));

        // 2. Register Blocks & Items (WITH COLOR COMPONENT!)
        // This is the change: We pass the color to the helper method.
        registerBlockWithColor(leavesName, leaves, tree.getColor());

        // You can do the same for fruit if you want colored fruit items
        registerBlockWithColor(fruitName, fruit, tree.getColor());

        // Saplings usually have unique textures, so we register them normally
        registerBlock(saplingName, sapling);

        // 3. Link to Tree Object
        tree.setLog(LIVING_LOG);
        tree.setLeaves(leaves);
        tree.setFruit(fruit);
    }

    // --- HELPER METHODS ---

    // NEW: Registers a block and automatically dyes its Item representation
    private static Block registerBlockWithColor(String name, Block block, int color) {
        Identifier id = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name);

        // 1. Register the Block
        Registry.register(BuiltInRegistries.BLOCK, id, block);

        // 2. Register the Item with DYED_COLOR component
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

        Item.Properties props = new Item.Properties()
                .setId(itemKey)
                // This line makes the inventory item colored!
                .component(DataComponents.DYED_COLOR, new DyedItemColor(color));

        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, props));

        return block;
    }

    // Standard Register (No Color)
    private static Block registerBlock(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties().setId(itemKey)));
        return block;
    }
}