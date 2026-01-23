package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    // 1. Define the Key
    public static final ResourceKey<CreativeModeTab> CAMBIUM_GROUP_KEY = ResourceKey.create(registries.CREATIVE_MODE_TAB, Identifier.of(Cambium.MOD_ID, "cambium_group"));

    // 2. Register the Group
    public static final CreativeModeTab CAMBIUM_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CAMBIUM_GROUP_KEY,
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.SOLAR_DIGESTER)) // The icon on the tab
                    .title(Component.translatable("itemGroup.cambium")) // The hover text
                    .displayItems((context, entries) -> {
                        // --- Add your items here ---
                        entries.accept(ModBlocks.SOLAR_DIGESTER);
                        entries.accept(ModBlocks.ROOT_BLOCK);
                        entries.accept(ModBlocks.MINERAL_SOIL);
                        entries.accept(ModBlocks.LIVING_LOG);
                    })
                    .build());

    public static void registerItemGroups() {
        Cambium.LOGGER.info("Registering Item Groups for " + Cambium.MOD_ID);
    }
}