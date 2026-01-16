package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.block.IronFruitBlock;
import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel; // Important for dropping fruit
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class RootBlockEntity extends BlockEntity {

    private int timer = 0;
    private int growthIndex = 0;

    // This list stores the unique shape of this specific tree
    private final List<BuildStep> buildPlan = new ArrayList<>();
    private boolean isPlanGenerated = false;

    public RootBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROOT_BE, pos, state);
    }

    // --- THE GENERATOR (3D Logic) ---
    private void generateTree() {
        long seed = this.worldPosition.asLong();
        RandomSource random = RandomSource.create(seed);

        // 10% chance to be a "Runt"
        boolean isRunt = random.nextInt(10) == 0;

        // Determine Height (Let's make them slightly taller to compensate for the gaps)
        // Old: 4 to 6. New: 6 to 9.
        int height = isRunt ? (random.nextInt(2) + 4) : (random.nextInt(4) + 6);

        // Build Trunk
        for (int y = 1; y <= height; y++) {
            addStep(new Vec3i(0, y, 0), ModBlocks.LIVING_LOG.defaultBlockState());

            // --- THE FIX ---
            // 1. Must be above Y=2
            // 2. Must not be the very top (height)
            // 3. (y % 2 != 0) -> Only branch on ODD numbers (3, 5, 7...)
            if (y > 2 && y < height && (y % 2 != 0)) {

                if (isRunt) {
                    if (random.nextInt(10) < 4) {
                        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        generate3DBranch(y, dir, random);
                    }
                } else {
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        // slightly higher chance (60%) since we have fewer layers now
                        if (random.nextInt(10) < 6) {
                            generate3DBranch(y, dir, random);
                        }
                    }
                }
            }
        }

        // Top Crown
        generateLeafCluster(new Vec3i(0, height + 1, 0));
        if (!isRunt) {
            addStep(new Vec3i(0, height + 2, 0), persistentLeaves());
        }
    }

    private void generate3DBranch(int y, Direction dir, RandomSource random) {
        Vec3i dirVec = dir.getUnitVec3i(); // Use getUnitVec3i or getVector based on mappings

        Vec3i branchPos = new Vec3i(dirVec.getX(), y, dirVec.getZ());

        // Determine rotation: If growing North/South, use Z axis. If East/West, use X axis.
        Direction.Axis axis = dir.getAxis();
        BlockState logState = ModBlocks.LIVING_LOG.defaultBlockState()
                .setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, axis);

        addStep(branchPos, logState);

        // 2. Branch Tip (Leaves)
        Vec3i tipPos = new Vec3i(dirVec.getX() * 2, y, dirVec.getZ() * 2);
        generateLeafCluster(tipPos);

        // 3. Fruit (Hanging under the tip)
        if (random.nextBoolean()) {
            Vec3i fruitPos = new Vec3i(tipPos.getX(), tipPos.getY() - 1, tipPos.getZ());
            addStep(fruitPos, ModBlocks.IRON_FRUIT.defaultBlockState().setValue(IronFruitBlock.AGE, 0));
        }
    }

    private void generateLeafCluster(Vec3i center) {
        addStep(center, persistentLeaves());
        addStep(new Vec3i(center.getX() + 1, center.getY(), center.getZ()), persistentLeaves());
        addStep(new Vec3i(center.getX() - 1, center.getY(), center.getZ()), persistentLeaves());
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() + 1), persistentLeaves());
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() - 1), persistentLeaves());
    }

    private void addStep(Vec3i offset, BlockState state) {
        buildPlan.add(new BuildStep(offset, state));
    }

    private BlockState persistentLeaves() {
        return ModBlocks.LIVING_LEAVES.defaultBlockState();
    }

    // --- THE TICKER (Growth & Production) ---
    public static void tick(Level level, BlockPos pos, BlockState state, RootBlockEntity entity) {
        if (level.isClientSide()) return;

        // 1. Generate the plan ONCE
        if (!entity.isPlanGenerated) {
            entity.generateTree();
            entity.isPlanGenerated = true;
        }

        // 2. PRODUCTION MODE (Tree is fully grown)
        if (entity.growthIndex >= entity.buildPlan.size()) {
            entity.timer++;

            // Check every 1 second (20 ticks) for ripening
            if (entity.timer >= 20) {

                // Loop through the plan to find Fruit Nodes
                for (BuildStep step : entity.buildPlan) {
                    if (step.state().is(ModBlocks.IRON_FRUIT)) {

                        BlockPos fruitPos = pos.offset(step.offset());
                        BlockState currentBlock = level.getBlockState(fruitPos);

                        // CASE A: Empty? Grow a new baby fruit
                        if (level.isEmptyBlock(fruitPos)) {
                            // 10% chance to regrow
                            if (level.random.nextInt(10) == 0) {
                                level.setBlock(fruitPos, ModBlocks.IRON_FRUIT.defaultBlockState().setValue(IronFruitBlock.AGE, 0), 3);
                                level.levelEvent(2005, fruitPos, 0); // Bone meal effect
                            }
                        }
                        // CASE B: Existing Fruit? Ripen it
                        else if (currentBlock.is(ModBlocks.IRON_FRUIT)) {
                            int age = currentBlock.getValue(IronFruitBlock.AGE);

                            // 10% chance to age up
                            if (level.random.nextInt(10) == 0) {
                                if (age < 2) {
                                    level.setBlock(fruitPos, currentBlock.setValue(IronFruitBlock.AGE, age + 1), 3);
                                } else {
                                    // Age 2 = Ripe -> Drop it!
                                    if (level instanceof ServerLevel serverLevel) {
                                        ((IronFruitBlock) ModBlocks.IRON_FRUIT).dropFruit(serverLevel, fruitPos);
                                    }
                                }
                            }
                        }
                    }
                }
                entity.timer = 0;
            }
            return;
        }

        // 3. GROWTH MODE (Building the tree)
        entity.timer++;
        if (entity.timer >= 5) { // Fast growth (0.25s)

            BuildStep step = entity.buildPlan.get(entity.growthIndex);
            BlockPos targetPos = pos.offset(step.offset());

            if (level.isEmptyBlock(targetPos)) {
                level.setBlock(targetPos, step.state(), 3);
                // Play sound
                level.levelEvent(2001, targetPos, net.minecraft.world.level.block.Block.getId(step.state()));
            }

            entity.growthIndex++;
            entity.timer = 0;
        }
    }
    private boolean isGrown = false;

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("IsGrown", this.isGrown);
        output.putInt("GrowthIndex", this.growthIndex);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.isGrown = input.getBooleanOr("IsGrown", false);
        this.growthIndex = input.getIntOr("GrowthIndex", 0);
    }

// Record helper
record BuildStep(Vec3i offset, BlockState state){}
}