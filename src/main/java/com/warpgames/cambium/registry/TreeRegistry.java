package com.warpgames.cambium.registry;

import com.warpgames.cambium.content.ResourceTree;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;

public class TreeRegistry {
    public static final List<ResourceTree> TREES = new ArrayList<>();

    // Register Trees with Name, Color, and Drop Item
    public static final ResourceTree IRON = register("iron", 0xE0D3DE, Items.RAW_IRON);
    public static final ResourceTree GOLD = register("gold", 0xFFA500, Items.RAW_GOLD);
    public static final ResourceTree DIAMOND = register("diamond", 0xA9C5A0, Items.DIAMOND);
    public static final ResourceTree EMERALD = register("emerald", 0x4B644A, Items.EMERALD);

    // Call this from ModBlocks to load the class
    public static void init() {}

    private static ResourceTree register(String name, int color, Item drop) {
        ResourceTree tree = new ResourceTree(name, color, drop);
        TREES.add(tree);
        return tree;
    }
}