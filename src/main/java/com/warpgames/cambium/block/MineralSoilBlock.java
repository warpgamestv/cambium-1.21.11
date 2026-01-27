package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec; // REQUIRED IMPORT
import com.warpgames.cambium.block.entity.MineralSoilBlockEntity;
import com.warpgames.cambium.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // 1. Check if the item in hand is Organic Ash
        if (stack.is(ModItems.ORGANIC_ASH)) {

            // 2. Run logic (Server Side Only)
            if (!level.isClientSide()) {
                // Get the BlockEntity (The "Brain" of the block)
                if (level.getBlockEntity(pos) instanceof MineralSoilBlockEntity soilEntity) {

                    // Add Charge (You can tweak this number! 1 Ash = 10 Charge?)
                    soilEntity.addCharge(10);

                    // Consume 1 item from the player's hand
                    stack.shrink(1);

                    // Play a sound to confirm it worked (using Bone Meal sound for now)
                    level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

                    // Update the block state if you have visual changes (optional)
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("Soil Charge: " + soilEntity.getCharge()), true);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}