package com.warpgames.cambium.registry;

import com.warpgames.cambium.content.ResourceTree;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;

public class TreeRegistry {

    public static final List<ResourceTree> TREES = new ArrayList<>();

    // Define your trees here!
    public static final ResourceTree IRON = register("iron", 0xD8AF93, Items.RAW_IRON);
    public static final ResourceTree GOLD = register("gold", 0xFDF55F, Items.RAW_GOLD);
    // Future: if (FabricLoader.isModLoaded("techreborn")) register("tin", ...);

    private static ResourceTree register(String name, int color, net.minecraft.world.item.Item seed) {
        ResourceTree tree = new ResourceTree(name, color, seed);
        TREES.add(tree);
        return tree;
    }

    public static void init() {
        // Just calling this class loads the static list
    }
}