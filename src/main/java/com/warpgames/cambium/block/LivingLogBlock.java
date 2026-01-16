package com.warpgames.cambium.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

// RotatedPillarBlock handles the "Sideways" placement logic automatically
public class LivingLogBlock extends RotatedPillarBlock {
    public LivingLogBlock(Properties properties) {
        super(properties);
    }
}