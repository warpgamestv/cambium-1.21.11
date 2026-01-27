package com.warpgames.cambium.block.entity;

import com.mojang.serialization.Codec;
import com.warpgames.cambium.block.ResourceFruitBlock;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
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
    private String treeType = "";

    private final List<BuildStep> buildPlan = new ArrayList<>();
    private boolean isPlanGenerated = false;

    public RootBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROOT_BE, pos, state);
    }

    // --- GETTERS & SETTERS ---
    public void setTreeType(String name) {
        this.treeType = name;
        setChanged();
    }

    private ResourceTree getTree() {
        return TreeRegistry.TREES.stream()
                .filter(t -> t.getName().equals(this.treeType))
                .findFirst()
                .orElse(TreeRegistry.IRON);
    }

    // --- SOIL CHECK (Restored!) ---
    private boolean hasValidSoil(Level level, BlockPos pos) {
        BlockPos soilPos = pos.below();
        BlockState soilState = level.getBlockState(soilPos);
        return soilState.is(ModBlocks.MINERAL_SOIL);
    }

    // --- THE GENERATOR ---
    private void generateTree() {
        long seed = this.worldPosition.asLong();
        RandomSource random = RandomSource.create(seed);

        ResourceTree treeDef = getTree();
        BlockState logState = ModBlocks.LIVING_LOG.defaultBlockState();
        BlockState leafState = treeDef.getLeaves().defaultBlockState();
        BlockState fruitState = treeDef.getFruit().defaultBlockState().setValue(ResourceFruitBlock.AGE, 0);

        boolean isRunt = random.nextInt(10) == 0;
        int height = isRunt ? (random.nextInt(2) + 4) : (random.nextInt(4) + 6);

        for (int y = 1; y <= height; y++) {
            addStep(new Vec3i(0, y, 0), logState);

            if (y > 2 && y < height && (y % 2 != 0)) {
                if (isRunt) {
                    if (random.nextInt(10) < 4) {
                        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        generate3DBranch(y, dir, random, logState, leafState, fruitState);
                    }
                } else {
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        if (random.nextInt(10) < 6) {
                            generate3DBranch(y, dir, random, logState, leafState, fruitState);
                        }
                    }
                }
            }
        }

        generateLeafCluster(new Vec3i(0, height + 1, 0), leafState);
        if (!isRunt) {
            addStep(new Vec3i(0, height + 2, 0), leafState);
        }
    }

    private void generate3DBranch(int y, Direction dir, RandomSource random, BlockState logState, BlockState leafState, BlockState fruitState) {
        Vec3i dirVec = dir.getUnitVec3i();
        Vec3i branchPos = new Vec3i(dirVec.getX(), y, dirVec.getZ());
        Direction.Axis axis = dir.getAxis();
        BlockState rotatedLog = logState.setValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, axis);
        addStep(branchPos, rotatedLog);

        Vec3i tipPos = new Vec3i(dirVec.getX() * 2, y, dirVec.getZ() * 2);
        generateLeafCluster(tipPos, leafState);

        if (random.nextBoolean()) {
            Vec3i fruitPos = new Vec3i(tipPos.getX(), tipPos.getY() - 1, tipPos.getZ());
            addStep(fruitPos, fruitState);
        }
    }

    private void generateLeafCluster(Vec3i center, BlockState leafState) {
        addStep(center, leafState);
        addStep(new Vec3i(center.getX() + 1, center.getY(), center.getZ()), leafState);
        addStep(new Vec3i(center.getX() - 1, center.getY(), center.getZ()), leafState);
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() + 1), leafState);
        addStep(new Vec3i(center.getX(), center.getY(), center.getZ() - 1), leafState);
    }

    private void addStep(Vec3i offset, BlockState state) {
        buildPlan.add(new BuildStep(offset, state));
    }

    // --- THE TICKER ---
    public static void tick(Level level, BlockPos pos, BlockState state, RootBlockEntity entity) {
        if (level.isClientSide()) return;

        // 1. GENERATION PHASE (Unchanged)
        if (!entity.isPlanGenerated) {
            if (entity.hasValidSoil(level, pos)) {
                entity.generateTree();
                entity.isPlanGenerated = true;
                entity.setChanged();
            }
            return;
        }

        // 2. PRODUCTION PHASE (The Fruit Loop)
        if (entity.growthIndex >= entity.buildPlan.size()) {

            entity.timer++;
            if (entity.timer >= 20) {
                ResourceTree treeDef = entity.getTree();

                for (BuildStep step : entity.buildPlan) {
                    if (step.state().getBlock() == treeDef.getFruit()) {

                        BlockPos fruitPos = pos.offset(step.offset());
                        BlockState currentBlock = level.getBlockState(fruitPos);

                        // LOGIC: Random Chance -> Check Charge -> Action
                        if (level.random.nextInt(10) == 0) {

                            // A. REGROWTH (Air -> Stage 0)
                            if (level.isEmptyBlock(fruitPos)) {
                                if (entity.tryUseSoilCharge(level, pos)) {
                                    level.setBlock(fruitPos, treeDef.getFruit().defaultBlockState().setValue(ResourceFruitBlock.AGE, 0), 3);
                                    level.levelEvent(2005, fruitPos, 0); // Bonemeal sound
                                }
                            }
                            // B. AGING (Stage 0 -> 1 -> 2 -> Drop)
                            else if (currentBlock.getBlock() == treeDef.getFruit()) {
                                int age = currentBlock.getValue(ResourceFruitBlock.AGE);
                                if (age < 2) {
                                    if (entity.tryUseSoilCharge(level, pos)) {
                                        level.setBlock(fruitPos, currentBlock.setValue(ResourceFruitBlock.AGE, age + 1), 3);
                                    }
                                } else {
                                    if (level instanceof ServerLevel serverLevel && currentBlock.getBlock() instanceof ResourceFruitBlock rfb) {
                                        rfb.dropFruit(serverLevel, fruitPos);
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

        // 3. GROWTH ANIMATION PHASE
        entity.timer++;
        if (entity.timer >= 5) {
            BuildStep step = entity.buildPlan.get(entity.growthIndex);
            BlockPos targetPos = pos.offset(step.offset());
            if (level.isEmptyBlock(targetPos)) {
                level.setBlock(targetPos, step.state(), 3);
                level.levelEvent(2001, targetPos, net.minecraft.world.level.block.Block.getId(step.state()));
                if (step.state().getBlock() instanceof LeavesBlock) {
                    level.scheduleTick(targetPos, step.state().getBlock(), 1);
                }
            }
            entity.growthIndex++;
            entity.timer = 0;
        }
    }

    // --- SAVING & LOADING ---
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("TreeType", Codec.STRING, this.treeType);
        output.store("GrowthIndex", Codec.INT, this.growthIndex);
        output.store("IsPlanGenerated", Codec.BOOL, this.isPlanGenerated);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.treeType = input.read("TreeType", Codec.STRING).orElse("");
        this.growthIndex = input.read("GrowthIndex", Codec.INT).orElse(0);
        this.isPlanGenerated = input.read("IsPlanGenerated", Codec.BOOL).orElse(false);
    }

    private boolean tryUseSoilCharge(Level level, BlockPos rootPos) {
        BlockPos soilPos = rootPos.below();
        if (level.getBlockEntity(soilPos) instanceof MineralSoilBlockEntity soil) {
            return soil.tryConsumeCharge(2);
        }
        return false;
    }

    record BuildStep(Vec3i offset, BlockState state){}
}