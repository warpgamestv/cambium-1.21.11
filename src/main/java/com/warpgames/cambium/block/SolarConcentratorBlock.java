package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec;
import com.warpgames.cambium.block.entity.SolarConcentratorBlockEntity;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarConcentratorBlock extends BaseEntityBlock {
    public static final MapCodec<SolarConcentratorBlock> CODEC = simpleCodec(SolarConcentratorBlock::new);

    public SolarConcentratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarConcentratorBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // This allows the block to "tick" its logic
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.SOLAR_CONCENTRATOR_BE, SolarConcentratorBlockEntity::tick);
    }
}