package com.warpgames.cambium.block;

import com.mojang.serialization.MapCodec;
import com.warpgames.cambium.block.entity.RootBlockEntity;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ResourceSaplingBlock extends BushBlock {

    // Codec required for 1.21+
    // Note: Passing 'null' here is a temporary hack to satisfy the crash.
    // Ideally, you register a separate Block class for every tree type, but this works for now.
    public static final MapCodec<BushBlock> CODEC = simpleCodec(properties -> new ResourceSaplingBlock(null, properties));

    private final ResourceTree tree;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public ResourceSaplingBlock(ResourceTree tree, Properties properties) {
        super(properties);
        this.tree = tree;
    }

    @Override
    public MapCodec<BushBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // --- PLACEMENT LOGIC ---
    // Allow planting on Dirt, Grass, OR Mineral Soil
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.MINERAL_SOIL) || super.mayPlaceOn(state, level, pos);
    }

    // --- GROWTH LOGIC ---
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 1. Check light level (Standard vanilla sapling requirement)
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {

            // 2. STAGE CHECK: ONLY grow if sitting on Mineral Soil
            if (level.getBlockState(pos.below()).is(ModBlocks.MINERAL_SOIL)) {
                grow(level, pos);
            }
        }
    }

    public void grow(ServerLevel level, BlockPos pos) {
        // REMOVED: level.setBlock(pos.below(), ModBlocks.MINERAL_SOIL...);
        // We no longer auto-place soil. The player must have done it.

        // 1. Transform into Root Block
        BlockState rootState = ModBlocks.ROOT_BLOCK.defaultBlockState();
        level.setBlock(pos, rootState, 3);

        // 2. Inject DNA (Fixes the "Gold Sapling is Useless" issue)
        if (level.getBlockEntity(pos) instanceof RootBlockEntity rootEntity) {
            // Safety check: ensure 'tree' isn't null (due to the codec hack)
            if (this.tree != null) {
                rootEntity.setTreeType(this.tree.getName());
                rootEntity.setChanged();
            }
        }
    }
}