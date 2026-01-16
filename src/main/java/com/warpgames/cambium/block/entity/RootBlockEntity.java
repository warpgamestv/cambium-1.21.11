package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RootBlockEntity extends BlockEntity {

    private int timer = 0;
    private int growthIndex = 0;

    // This list is not static anymore! It is unique for every single tree.
    private final List<BuildStep> buildPlan = new ArrayList<>();
    private boolean isPlanGenerated = false;

    public RootBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROOT_BE, pos, state);
    }

    // --- THE GENERATOR ---
    private void generateTree() {
        long seed = this.worldPosition.asLong();
        RandomSource random = RandomSource.create(seed);

        // DECISION: Is this a "Healthy" tree or a "Failed" tree?
        // 10% chance to be a "Runt" (Sparse/Small)
        boolean isRunt = random.nextInt(10) == 0;

        // 1. DETERMINE HEIGHT
        // Healthy: 6 to 9 blocks tall
        // Runt: 4 to 5 blocks tall
        int height = isRunt ? (random.nextInt(2) + 4) : (random.nextInt(4) + 6);

        // 2. BUILD TRUNK
        for (int y = 1; y <= height; y++) {
            addStep(new Vec3i(0, y, 0), ModBlocks.LIVING_LOG.defaultBlockState());

            // 3. GENERATE BRANCHES
            // Only start branching after Y=2 to leave room at the base
            if (y > 2 && y < height) {
                if (isRunt) {
                    // RUNT LOGIC: Only try ONE direction per layer (Sparse/Flat)
                    if (random.nextInt(10) < 4) { // 40% chance
                        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        generate3DBranch(y, dir, random);
                    }
                } else {
                    // HEALTHY LOGIC: Try ALL 4 directions per layer (Full/3D)
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        // 50% chance for a branch in THIS direction
                        // This creates "Whorls" of branches, filling out the tree
                        if (random.nextBoolean()) {
                            generate3DBranch(y, dir, random);
                        }
                    }
                }
            }
        }

        // 4. TOP CROWN
        // Healthy trees get a bigger top cluster
        generateLeafCluster(new Vec3i(0, height + 1, 0));
        if (!isRunt) {
            // Add extra leaves on top to make the crown pointy/taller
            addStep(new Vec3i(0, height + 2, 0), persistentLeaves());
        }
    }

    private void generate3DBranch(int y, Direction dir, RandomSource random) {
        // Get the coordinate for the direction (e.g., North is 0,0,-1)
        Vec3i dirVec = dir.getUnitVec3i();

        // 1. Branch Log (1 block out from trunk)
        // Formula: (0,y,0) + (dx, dy, dz)
        Vec3i branchPos = new Vec3i(dirVec.getX(), y, dirVec.getZ());
        addStep(branchPos, ModBlocks.LIVING_LOG.defaultBlockState());

        // 2. Branch Tip (2 blocks out)
        Vec3i tipPos = new Vec3i(dirVec.getX() * 2, y, dirVec.getZ() * 2);

        // 3. Generate a Cluster of Leaves at the tip
        generateLeafCluster(tipPos);

        // 4. Hang Fruit (50% chance per branch)
        if (random.nextBoolean()) {
            // Hang it under the tip
            Vec3i fruitPos = new Vec3i(tipPos.getX(), tipPos.getY() - 1, tipPos.getZ());
            addStep(fruitPos, ModBlocks.IRON_FRUIT.defaultBlockState());
        }
    }

    private void generateLeafCluster(Vec3i center) {
        // Center Leaf
        addStep(center, persistentLeaves());

        // Surrounding Leaves (North, South, East, West of the center)
        // This creates a "+" shape canopy
        addStep(new Vec3i(center.getX() + 1, center.getY(), center.getZ()), persistentLeaves());
        addStep(new Vec3i(center.getX() - 1, center.getY(), center.getZ()), persistentLeaves());
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() + 1), persistentLeaves());
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() - 1), persistentLeaves());
    }

    // Helper to make code cleaner
    private void addStep(Vec3i offset, BlockState state) {
        buildPlan.add(new BuildStep(offset, state));
    }

    // Helper for Persistent Leaves
    private BlockState persistentLeaves() {
        return ModBlocks.LIVING_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
    }

    // --- THE TICKER ---
    public static void tick(Level level, BlockPos pos, BlockState state, RootBlockEntity entity) {
        if (level.isClientSide()) return;

        // 1. Generate the plan ONCE
        if (!entity.isPlanGenerated) {
            entity.generateTree();
            entity.isPlanGenerated = true;
        }

        // 2. Stop if finished
        if (entity.growthIndex >= entity.buildPlan.size()) {
            // (Optional: Add your Regrowth Logic here later)
            return;
        }

        entity.timer++;
        if (entity.timer >= 10) { // Fast growth (0.5 seconds per block)

            // Get the next instruction
            BuildStep step = entity.buildPlan.get(entity.growthIndex);
            BlockPos targetPos = pos.offset(step.offset());

            // Place block
            if (level.isEmptyBlock(targetPos)) {
                level.setBlock(targetPos, step.state(), 3);
                level.levelEvent(2001, targetPos, net.minecraft.world.level.block.Block.getId(step.state()));
            }

            entity.growthIndex++;
            entity.timer = 0;
        }
    }
}

// Don't forget the record at the bottom!
record BuildStep(Vec3i offset, BlockState state) {}