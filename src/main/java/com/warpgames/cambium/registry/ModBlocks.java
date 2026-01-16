package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium; // Import your main class
import com.warpgames.cambium.block.IronFruitBlock;
import com.warpgames.cambium.block.LivingLeavesBlock;
import com.warpgames.cambium.block.LivingLogBlock;
import com.warpgames.cambium.block.RootBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final ResourceKey<Block> ROOT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "root_block"));
    // Define the Root Block
    // We give it "Wood" properties so it burns and sounds like wood
    public static final Block ROOT_BLOCK = registerBlock("root_block",
            new RootBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .setId(ROOT_BLOCK_KEY)));

    public static final ResourceKey<Block> LIVING_LOG_KEY = ResourceKey.create(
            Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "living_log"));

    public static final Block LIVING_LOG = registerBlock("living_log",
            new LivingLogBlock(BlockBehaviour.Properties.of()
                    .setId(LIVING_LOG_KEY)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()));

    // 2. LIVING LEAVES
    public static final ResourceKey<Block> LIVING_LEAVES_KEY = ResourceKey.create(
            Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "living_leaves"));

    public static final Block LIVING_LEAVES = registerBlock("living_leaves",
            new LivingLeavesBlock(BlockBehaviour.Properties.of()
                    .setId(LIVING_LEAVES_KEY)
                    .strength(0.2f)
                    .sound(SoundType.GRASS)
                    .noOcclusion() // Important for transparency
                    .ignitedByLava()));

    // Helper method to register the Block AND the Item (so you can hold it)
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(
                BuiltInRegistries.BLOCK,
                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name),
                block);
    }

    public static final ResourceKey<Block> IRON_FRUIT_KEY = ResourceKey.create(
            Registries.BLOCK, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "iron_fruit"));

    public static final Block IRON_FRUIT = registerBlock("iron_fruit",
            new IronFruitBlock(BlockBehaviour.Properties.of()
                    .setId(IRON_FRUIT_KEY)
                    .strength(0.5f) // Easy to break
                    .sound(SoundType.GLASS) // Sounds "tinkly" like metal
                    .noOcclusion())); // Allow transparency

    // Helper method to register the Item
    private static void registerBlockItem(String name, Block block) {
        ResourceKey<Item> itemKey = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name)
        );
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name),
                new BlockItem(block, new Item.Properties().setId(itemKey)));
    }

    // Call this method in your Main Class to load everything
    public static void registerModBlocks() {
        Cambium.LOGGER.info("Registering Mod Blocks for " + Cambium.MOD_ID);
    }
}