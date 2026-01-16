package com.warpgames.cambium.block;

import com.warpgames.cambium.block.entity.MineralSoilBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MineralSoilBlock extends BaseEntityBlock {

    public static final MapCodec<MineralSoilBlock> CODEC = simpleCodec(MineralSoilBlock::new);

    public MineralSoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MineralSoilBlockEntity(pos, state);
    }

    // --- 1.21.11 DROP LOGIC ---
    // Using playerDestroy because 'onRemove' is missing in your mappings.
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (blockEntity instanceof MineralSoilBlockEntity) {
            Containers.dropContents(level, pos, (MineralSoilBlockEntity)blockEntity);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("Soil Inventory Accessed"), true);
        }
        return InteractionResult.SUCCESS;
    }
}