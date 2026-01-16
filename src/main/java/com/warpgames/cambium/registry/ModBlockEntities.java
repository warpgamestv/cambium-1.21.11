package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.entity.RootBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    // Define the Type
    public static final BlockEntityType<RootBlockEntity> ROOT_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "root_be"),
            // This builder connects the Entity to the Block (ModBlocks.ROOT_BLOCK)
            FabricBlockEntityTypeBuilder.create(RootBlockEntity::new, ModBlocks.ROOT_BLOCK).build(null)
    );

    public static void registerBlockEntities() {
        Cambium.LOGGER.info("Registering Block Entities for " + Cambium.MOD_ID);
    }
}