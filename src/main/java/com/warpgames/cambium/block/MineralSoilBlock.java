package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec; // REQUIRED IMPORT
import com.warpgames.cambium.block.entity.MineralSoilBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MineralSoilBlock extends BaseEntityBlock {

    // 1. DEFINE THE CODEC (Fixes the abstract error)
    public static final MapCodec<MineralSoilBlock> CODEC = simpleCodec(MineralSoilBlock::new);

    public MineralSoilBlock(Properties properties) {
        super(properties);
    }

    // 2. IMPLEMENT THE CODEC METHOD
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MineralSoilBlockEntity(pos, state);
    }

    // --- THE STONE EATER LOGIC ---
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int fuelValue = getFuelValue(stack);

        if (fuelValue > 0) {
            if (level.getBlockEntity(pos) instanceof MineralSoilBlockEntity soil) {

                // Try to add charge (returns false if full)
                if (soil.addCharge(fuelValue)) {
                    level.playSound(player, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    // Helper to define what rocks give what charge
    private int getFuelValue(ItemStack stack) {
        if (stack.is(Items.COBBLESTONE)) return 5;
        if (stack.is(Items.COBBLED_DEEPSLATE)) return 8;
        if (stack.is(Items.ANDESITE)) return 5;
        if (stack.is(Items.DIORITE)) return 5;
        if (stack.is(Items.GRANITE)) return 5;
        if (stack.is(Items.TUFF)) return 5;
        if (stack.is(Items.DRIPSTONE_BLOCK)) return 6;
        return 0;
    }
}