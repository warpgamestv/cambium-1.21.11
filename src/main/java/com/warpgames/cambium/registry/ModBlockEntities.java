package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.entity.MineralSoilBlockEntity;
import com.warpgames.cambium.block.entity.RootBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    // Define the Type
    public static final BlockEntityType<MineralSoilBlockEntity> MINERAL_SOIL_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath("cambium", "mineral_soil_be"),
            FabricBlockEntityTypeBuilder.create(MineralSoilBlockEntity::new, ModBlocks.MINERAL_SOIL).build(null) // Only Soil!
    );

    public static final BlockEntityType<RootBlockEntity> ROOT_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath("cambium", "root_be"),
            FabricBlockEntityTypeBuilder.create(RootBlockEntity::new, ModBlocks.ROOT_BLOCK).build(null) // Only Root!
    );

    public static void registerBlockEntities() {
        Cambium.LOGGER.info("Registering Block Entities for " + Cambium.MOD_ID);
    }
}