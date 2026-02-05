package com.warpgames.cambium.block.transport;

import com.warpgames.cambium.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.Container;

import java.util.EnumMap;
import java.util.Map;

public class PhloemDuctBlock extends Block implements SimpleWaterloggedBlock {

    // Properties: Now using ENUMS instead of Booleans
    public static final EnumProperty<DuctConnection> NORTH = EnumProperty.create("north", DuctConnection.class);
    public static final EnumProperty<DuctConnection> EAST = EnumProperty.create("east", DuctConnection.class);
    public static final EnumProperty<DuctConnection> SOUTH = EnumProperty.create("south", DuctConnection.class);
    public static final EnumProperty<DuctConnection> WEST = EnumProperty.create("west", DuctConnection.class);
    public static final EnumProperty<DuctConnection> UP = EnumProperty.create("up", DuctConnection.class);
    public static final EnumProperty<DuctConnection> DOWN = EnumProperty.create("down", DuctConnection.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // Shapes
    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5));
        SHAPES.put(Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16));
        SHAPES.put(Direction.EAST, Block.box(11, 5, 5, 16, 11, 11));
        SHAPES.put(Direction.WEST, Block.box(0, 5, 5, 5, 11, 11));
        SHAPES.put(Direction.UP, Block.box(5, 11, 5, 11, 16, 11));
        SHAPES.put(Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11));
    }

    public PhloemDuctBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, DuctConnection.NONE).setValue(EAST, DuctConnection.NONE)
                .setValue(SOUTH, DuctConnection.NONE).setValue(WEST, DuctConnection.NONE)
                .setValue(UP, DuctConnection.NONE).setValue(DOWN, DuctConnection.NONE)
                .setValue(WATERLOGGED, false));
    }

    // --- INTERACTION: The Grafting Tool Logic ---
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // If we are holding the Grafting Tool
        ItemStack heldItem = player.getMainHandItem();
        // NOTE: Replace 'ModItems.GRAFTING_TOOL' with your actual item registry later
        if (heldItem.getItem() == ModItems.GRAFTING_TOOL) {

            if (level.isClientSide()) return InteractionResult.SUCCESS;

            // 1. Determine which face was clicked
            Direction clickedFace = hitResult.getDirection();

            // 2. Identify the side of the duct we want to modify.
            // (Usually, if you click the North face of the block, you are modifying the North connection)
            // However, pipes are tricky. Let's assume we modify the side relative to the hit vector.
            // Simpler approach: Modify the side the player CLICKED ON.

            // Get the current state of that side
            EnumProperty<DuctConnection> property = getProperty(clickedFace);
            DuctConnection current = state.getValue(property);

            // 3. Cycle: NONE -> NORMAL -> EXTRACT -> NONE
            DuctConnection next = switch (current) {
                case NONE -> DuctConnection.NORMAL;
                case NORMAL -> DuctConnection.EXTRACT;
                case EXTRACT -> DuctConnection.NONE;
            };

            // 4. Update Block
            level.setBlock(pos, state.setValue(property, next), 3);

            // Play a sound (Snip or squelch)
            // level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1f, 1f);

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // --- SHAPE LOGIC ---
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        // If connection is NOT none (Normal OR Extract), add the shape
        if (state.getValue(NORTH) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.NORTH));
        if (state.getValue(SOUTH) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.SOUTH));
        if (state.getValue(EAST) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.EAST));
        if (state.getValue(WEST) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.WEST));
        if (state.getValue(UP) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.UP));
        if (state.getValue(DOWN) != DuctConnection.NONE) shape = Shapes.or(shape, SHAPES.get(Direction.DOWN));
        return shape;
    }

    // --- CONNECTION LOGIC ---
    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);

        // Start with default state (Waterlogged check)
        BlockState state = this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

        // Check all 6 neighbors and set connections
        return state
                .setValue(NORTH, getInitialConnection(level, pos.north(), Direction.SOUTH))
                .setValue(SOUTH, getInitialConnection(level, pos.south(), Direction.NORTH))
                .setValue(EAST, getInitialConnection(level, pos.east(), Direction.WEST))
                .setValue(WEST, getInitialConnection(level, pos.west(), Direction.EAST))
                .setValue(UP, getInitialConnection(level, pos.above(), Direction.DOWN))
                .setValue(DOWN, getInitialConnection(level, pos.below(), Direction.UP));
    }

    private DuctConnection getInitialConnection(Level level, BlockPos neighborPos, Direction dir) {
        if (canConnect(level, neighborPos, dir)) {
            return DuctConnection.NORMAL;
        }
        return DuctConnection.NONE;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        // We only Auto-Connect if the current state is NONE or NORMAL.
        // We do NOT want to overwrite a player's manual "EXTRACT" setting or explicit "NONE" disconnect.

        // Actually, for simplicity in early versions:
        // If the neighbor is valid AND we are currently NONE, connect as NORMAL.
        // If the neighbor becomes invalid (air), set to NONE.

        EnumProperty<DuctConnection> prop = getProperty(direction);
        DuctConnection current = state.getValue(prop);
        boolean isValidNeighbor = canConnect(level, neighborPos, direction);

        if (!isValidNeighbor) {
            return state.setValue(prop, DuctConnection.NONE);
        } else if (current == DuctConnection.NONE) {
            // Auto-connect new valid neighbors
            return state.setValue(prop, DuctConnection.NORMAL);
        }

        return state;
    }

    private EnumProperty<DuctConnection> getProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    private boolean canConnect(LevelAccessor level, BlockPos pos, Direction dir) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PhloemDuctBlock) return true;
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof Container;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}