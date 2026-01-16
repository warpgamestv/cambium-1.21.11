package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.renderer.state.ParticlesRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LivingLeavesBlock extends LeavesBlock {
    public static final MapCodec<LivingLeavesBlock> CODEC = simpleCodec(LivingLeavesBlock::new);
    public LivingLeavesBlock(Properties properties) {
        super(0.5f, properties);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource randomSource) {

    }

    // Vanilla LeavesBlock already handles decay and transparency logic!
}