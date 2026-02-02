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

    // --- VANILLA ---
    public static final ResourceTree IRON = register("iron", 0xE0D3DE, () -> Items.RAW_IRON);
    public static final ResourceTree GOLD = register("gold", 0xFFA500, () -> Items.RAW_GOLD);
    public static final ResourceTree COPPER = register("copper", 0xB87333, () -> Items.RAW_COPPER);
    public static final ResourceTree DIAMOND = register("diamond", 0xA9C5A0, () -> Items.DIAMOND);
    public static final ResourceTree EMERALD = register("emerald", 0x4B644A, () -> Items.EMERALD);
    public static final ResourceTree REDSTONE = register("redstone", 0xFF0000, () -> Items.REDSTONE);
    public static final ResourceTree LAPIS = register("lapis", 0x0000FF, () -> Items.LAPIS_LAZULI);

    // --- MODDED ---
    // Note: We use the String ID version for consistency
    public static final ResourceTree BIOPOLYMER = register("biopolymer", 0x6A4B9C, "cambium:biopolymer");
    public static final ResourceTree VESPERITE = register("vesperite", 0x4B367C, "vesper_wilds:raw_vesperite");

    public static void init() {}

    // Method 1: Direct Supplier (Vanilla)
    private static ResourceTree register(String name, int color, Supplier<Item> drop) {
        // We pass the name as ID just for safety, but usually rely on Supplier here
        ResourceTree tree = new ResourceTree(name, "minecraft", color, drop, "minecraft:" + name);
        TREES.add(tree);
        return tree;
    }

    // Method 2: String Lookup (Modded)
    private static ResourceTree register(String name, int color, String itemId) {
        String[] parts = itemId.split(":");
        String modId = parts.length > 0 ? parts[0] : "minecraft";

        Supplier<Item> dropSupplier = () -> {
            Identifier id = Identifier.tryParse(itemId);
            if (id == null) return Items.AIR;
            return BuiltInRegistries.ITEM.get(id)
                    .map(Holder.Reference::value)
                    .orElse(Items.AIR);
        };

        // Pass itemId to the constructor so we can look it up later!
        ResourceTree tree = new ResourceTree(name, modId, color, dropSupplier, itemId);

        if (FabricLoader.getInstance().isModLoaded(modId) || modId.equals("minecraft") || modId.equals("cambium") || modId.equals("vesper_wilds")) {
            TREES.add(tree);
        }
        return tree;
    }
}