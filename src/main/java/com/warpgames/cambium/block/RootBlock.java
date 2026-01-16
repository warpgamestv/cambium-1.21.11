package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec;
import com.warpgames.cambium.block.entity.RootBlockEntity;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RootBlock extends BaseEntityBlock {
    public static final MapCodec<RootBlock> CODEC = simpleCodec(RootBlock::new);

    public RootBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    // CHANGE 2: The Render Shape
    // BaseEntityBlock defaults to INVISIBLE. We must set it back to MODEL so we can see it.
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // CHANGE 3: Create the BlockEntity when placed
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RootBlockEntity(pos, state);
    }

    // CHANGE 4: Connect the "tick" method
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Return our tick method if the type matches
        return createTickerHelper(type, ModBlockEntities.ROOT_BE, RootBlockEntity::tick);
    }

    // Keep your existing Right-Click code below...
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.literal("Hello, Cambium!"), false);
        }
        return InteractionResult.SUCCESS;
    }
}