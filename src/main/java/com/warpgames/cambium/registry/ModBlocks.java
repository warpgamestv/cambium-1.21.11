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

public class ModBlocks {

    // --- STATIC GENERIC BLOCKS (Safe/Untouched) ---

    public static final ResourceKey<Block> ROOT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "root_block"));
    public static final Block ROOT_BLOCK = registerBlock("root_block",
            new RootBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .setId(ROOT_BLOCK_KEY)));

    public static final ResourceKey<Block> LIVING_LOG_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "living_log"));
    public static final Block LIVING_LOG = registerBlock("living_log",
            new LivingLogBlock(BlockBehaviour.Properties.of()
                    .setId(LIVING_LOG_KEY)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()));

    public static final ResourceKey<Block> MINERAL_SOIL_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "mineral_soil"));
    public static final Block MINERAL_SOIL = registerBlock("mineral_soil",
            new MineralSoilBlock(BlockBehaviour.Properties.of()
                    .setId(MINERAL_SOIL_KEY)
                    .strength(1.0f)));

    // --- DYNAMIC BLOCKS (Populated by Loop) ---
    // Not 'final' anymore because they are assigned in the loop
    public static Block LIVING_LEAVES;
    public static Block IRON_FRUIT;

    public static void registerModBlocks() {
        Cambium.LOGGER.info("Registering Mod Blocks for " + Cambium.MOD_ID);

        // 1. Initialize Registry
        TreeRegistry.init();

        // 2. Loop through every tree and create blocks
        for (ResourceTree tree : TreeRegistry.TREES) {
            registerTreeBlocks(tree);
        }
    }

    private static void registerTreeBlocks(ResourceTree tree) {
        // PRESERVE LEGACY NAMES:
        // If the tree is "iron", we keep the old IDs ("living_leaves", "iron_fruit")
        // so we don't break your existing world.
        boolean isLegacyIron = tree.getName().equals("iron");

        String leavesName = isLegacyIron ? "living_leaves" : tree.getName() + "_leaves";
        String fruitName = isLegacyIron ? "iron_fruit" : tree.getName() + "_fruit";

        ResourceKey<Block> leavesKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, leavesName));
        ResourceKey<Block> fruitKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, fruitName));
        // 1. Register Leaves
        // Note: In the future, you can pass tree.getColor() to the block if needed
        Block leaves = registerBlock(leavesName,
                new LivingLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                        .strength(0.2f)
                        .sound(SoundType.GRASS)
                        .noOcclusion()
                        .setId(leavesKey)
                        .ignitedByLava()));

        // 2. Register Fruit
        // Note: Currently using IronFruitBlock. For "Gold", we will need a GenericFruitBlock later.
        Block fruit = registerBlock(fruitName,
                new ResourceFruitBlock(tree, BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA)
                        .setId(fruitKey)
                        .strength(0.5f)
                        .sound(SoundType.GLASS)
                        .noOcclusion()));

        // 3. Link blocks back to the Tree Definition
        tree.setBlocks(MINERAL_SOIL,ROOT_BLOCK, leaves, fruit);

        // 4. Assign to Static Fields (so other code doesn't crash)
        if (isLegacyIron) {
            LIVING_LEAVES = leaves;
            IRON_FRUIT = fruit;
        }
    }

    // --- HELPER METHODS ---
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);

        // Ensure ID is set on the block if not already (for the dynamic ones)
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name));
        // We can't easily set the Key on an already created block without a mixin or constructor,
        // but 'registry.register' handles the mapping.
        // The properties .setId() call is mostly for datafixers/advanced stuff.

        return Registry.register(
                BuiltInRegistries.BLOCK,
                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name),
                block);
    }

    private static void registerBlockItem(String name, Block block) {
        ResourceKey<Item> itemKey = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name)
        );
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name),
                new BlockItem(block, new Item.Properties().setId(itemKey)));
    }
}