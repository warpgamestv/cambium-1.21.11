package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;


public class ModItems {

    // --- HELPER METHOD ---
    private static Item registerItem(String name, Item item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                Identifier.fromNamespaceAndPath(Cambium.MOD_ID, name),
                item
        );
    }

    public static void registerModItems() {
        Cambium.LOGGER.info("Registering Mod Items for " + Cambium.MOD_ID);
    }
}