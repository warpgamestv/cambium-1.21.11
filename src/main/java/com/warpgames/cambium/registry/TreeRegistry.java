package com.warpgames.cambium.registry;

import net.fabricmc.loader.api.FabricLoader;
import com.warpgames.cambium.content.ResourceTree;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TreeRegistry {
    public static final List<ResourceTree> TREES = new ArrayList<>();

    // Register Trees with Name, Color, and Drop Item
    public static final ResourceTree IRON = register("iron", 0xE0D3DE, () -> Items.RAW_IRON);
    public static final ResourceTree GOLD = register("gold", 0xFFA500, () -> Items.RAW_GOLD);
    public static final ResourceTree COPPER = register("copper", 0xB87333, () -> Items.RAW_COPPER);
    public static final ResourceTree DIAMOND = register("diamond", 0xA9C5A0, () -> Items.DIAMOND);
    public static final ResourceTree EMERALD = register("emerald", 0x4B644A, () -> Items.EMERALD);

    // --- MODDED TREES ---
    public static final ResourceTree SAPPHIRE = register("sapphire", 0x1C4D8C, "geolosys:sapphire");
    public static final ResourceTree TIN = register("tin", 0x939996, "techreborn:tin_ingot");
    public static final ResourceTree POLYMER = register("polymer", 0x6A4B9C, "cambium:polymer");
    public static final ResourceTree VESPERITE = register("vesperite", 0x4B367C, "vesper_wilds:raw_vesperite");

    public static void init() {}

    private static ResourceTree register(String name, int color, Supplier<Item> drop) {
        // Pass "minecraft" explicitly
        ResourceTree tree = new ResourceTree(name, "minecraft", color, drop);
        TREES.add(tree);
        return tree;
    }

    // Method 2: For Modded Support (String Lookup)
    private static ResourceTree register(String name, int color, String itemId) {
        // 1. Parse the Mod ID
        String[] parts = itemId.split(":");
        String modId = parts.length > 0 ? parts[0] : "minecraft";

        // 2. Define the Supplier
        Supplier<Item> dropSupplier = () -> {
            Identifier id = Identifier.tryParse(itemId);
            if (id == null) return Items.AIR;
            return BuiltInRegistries.ITEM.get(id)
                    .map(Holder.Reference::value)
                    .orElse(Items.AIR);
        };
        ResourceTree tree = new ResourceTree(name, modId, color, dropSupplier);

        // 4. Only add to the list if the mod is loaded (or if it's minecraft/cambium)
        boolean isModLoaded = modId.equals("minecraft") ||
                modId.equals("cambium") ||
                modId.equals("vesper_wilds") ||
                FabricLoader.getInstance().isModLoaded(modId);

        if (isModLoaded) {
            TREES.add(tree);
        }

        return tree;
    }
}