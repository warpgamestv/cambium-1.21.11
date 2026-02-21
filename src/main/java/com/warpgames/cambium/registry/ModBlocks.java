package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.*;
import com.warpgames.cambium.block.transport.PhloemDuctBlock;
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
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {

        // --- STATIC GENERIC BLOCKS ---
        public static final ResourceKey<Block> ROOT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "root_block"));
        public static final Block ROOT_BLOCK = registerBlock("root_block",
                        new RootBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
                                        .strength(2.0f)
                                        .sound(SoundType.WOOD)
                                        .setId(ROOT_BLOCK_KEY)));

        public static final ResourceKey<Block> SOLAR_DIGESTER_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digester"));
        public static final Block SOLAR_DIGESTER = registerBlock("solar_digester",
                        new SolarDigesterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
                                        .strength(2.0f)
                                        .sound(SoundType.WOOD)
                                        .setId(SOLAR_DIGESTER_KEY)));

        public static final ResourceKey<Block> SOLAR_CONCENTRATOR_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "solar_concentrator"));
        public static final Block SOLAR_CONCENTRATOR = registerBlock("solar_concentrator",
                        new SolarConcentratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                                        .strength(2.0f)
                                        .sound(SoundType.IRON)
                                        .setId(SOLAR_CONCENTRATOR_KEY)));

        public static final ResourceKey<Block> GRAVITROPIC_NODE_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "gravitropic_node"));
        public static final Block GRAVITROPIC_NODE = registerBlock("gravitropic_node",
                        new GravitropicNodeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                                        .strength(2.0f)
                                        .sound(SoundType.IRON)
                                        .setId(GRAVITROPIC_NODE_KEY)));

        public static final ResourceKey<Block> LIVING_LOG_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "living_log"));
        public static final Block LIVING_LOG = registerBlock("living_log",
                        new LivingLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                                        .setId(LIVING_LOG_KEY)
                                        .strength(2.0f)
                                        .sound(SoundType.WOOD)
                                        .ignitedByLava()));

        public static final ResourceKey<Block> MINERAL_SOIL_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "mineral_soil"));
        public static final Block MINERAL_SOIL = registerBlock("mineral_soil",
                        new MineralSoilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
                                        .setId(MINERAL_SOIL_KEY)
                                        .sound(SoundType.GRAVEL)
                                        .strength(1.0f)));

        public static final ResourceKey<Block> PHLOEM_DUCT_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "phloem_duct"));
        public static final Block PHLOEM_DUCT = registerBlock("phloem_duct",
                        new PhloemDuctBlock(BlockBehaviour.Properties.of()
                                        .setId(PHLOEM_DUCT_KEY)
                                        .mapColor(MapColor.WOOD) // Map color
                                        .strength(2.0f) // Hardness (like wood)
                                        .sound(SoundType.WOOD) // Sound when walking/breaking
                                        .noOcclusion() // CRITICAL: Tells game it's not a full solid cube
                                        .isRedstoneConductor((state, world, pos) -> false) // Prevent it from
                                                                                           // transmitting redstone
                                                                                           // power physically
                        ));

        public static final ResourceKey<Block> MYCELIAL_STRAND_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "mycelial_strand"));
        public static final Block MYCELIAL_STRAND = registerBlock("mycelial_strand",
                        new MycelialStrandBlock(BlockBehaviour.Properties.of()
                                        .setId(MYCELIAL_STRAND_KEY)
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .strength(0.5f)
                                        .sound(SoundType.SLIME_BLOCK)
                                        .noOcclusion()));

        public static final ResourceKey<Block> MYCELIAL_NODE_KEY = ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "mycelial_node"));
        public static final Block MYCELIAL_NODE = registerBlock("mycelial_node",
                        new MycelialNodeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUSHROOM_STEM)
                                        .setId(MYCELIAL_NODE_KEY)
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .strength(1.5f)
                                        .sound(SoundType.WOOD)));

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

                ResourceKey<Block> leavesKey = ResourceKey.create(Registries.BLOCK,
                                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, leavesName));
                ResourceKey<Block> fruitKey = ResourceKey.create(Registries.BLOCK,
                                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, fruitName));
                ResourceKey<Block> saplingKey = ResourceKey.create(Registries.BLOCK,
                                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, saplingName));

                // 1. Create Blocks
                Block leaves = new LivingLeavesBlock(tree, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                                .setId(leavesKey).strength(0.2f).sound(SoundType.GRASS).noOcclusion().ignitedByLava());

                Block fruit = new ResourceFruitBlock(tree, BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA)
                                .setId(fruitKey).strength(0.5f).sound(SoundType.GLASS).noOcclusion());

                Block sapling = new ResourceSaplingBlock(tree, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
                                .setId(saplingKey).noOcclusion().sound(SoundType.GRASS).instabreak().noCollision());

                // 2. Register Blocks & Items (WITH COLOR COMPONENT!)
                registerBlockWithColor(leavesName, leaves, tree.getColor());
                registerBlockWithColor(fruitName, fruit, tree.getColor());
                registerBlockWithColor(saplingName, sapling, tree.getColor());

                tree.setLog(LIVING_LOG);
                tree.setLeaves(leaves);
                tree.setFruit(fruit);
                tree.setSapling(sapling);
        }

        // --- HELPER METHODS ---

        private static Block registerBlockWithColor(String name, Block block, int color) {
                Identifier id = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name);
                ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
                ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

                // 1. Register Block
                Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

                // 2. Register Item with matching ID and Color Component
                Item.Properties props = new Item.Properties()
                                .setId(itemKey) // This MUST match the block's path
                                .component(DataComponents.DYED_COLOR, new DyedItemColor(color));

                Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, props));

                return block;
        }

        private static Block registerBlock(String name, Block block) {
                Identifier id = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name);
                ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
                ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

                // 1. Register Block
                Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

                // 2. Register Item
                Registry.register(BuiltInRegistries.ITEM, itemKey,
                                new BlockItem(block, new Item.Properties().setId(itemKey)));

                return block;
        }
}