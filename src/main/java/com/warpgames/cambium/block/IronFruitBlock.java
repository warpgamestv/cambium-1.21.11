package com.warpgames.cambium.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronFruitBlock extends Block {

    // Make it smaller than a full block (like a cocoa bean or lantern)
    private static final VoxelShape SHAPE = Block.box(4, 8, 4, 12, 16, 12);

    public IronFruitBlock(Properties properties) {
        super(properties);
    }

    // This makes the block look smaller in the world
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}