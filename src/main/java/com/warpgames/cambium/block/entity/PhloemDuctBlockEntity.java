package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.block.transport.DuctConnection;
import com.warpgames.cambium.block.transport.PhloemDuctBlock;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
// Ensure your IDE imports the correct ValueOutput/ValueInput from your mappings
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class PhloemDuctBlockEntity extends BlockEntity implements WorldlyContainer {

    private ItemStack heldItem = ItemStack.EMPTY;
    private int cooldown = 0;

    // CHANGE 1: Track where the item came from to prevent backflow
    private Direction lastInputDir = null;

    public PhloemDuctBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PHLOEM_DUCT, pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PhloemDuctBlockEntity entity) {
        if (entity.cooldown > 0) {
            entity.cooldown--;
            return;
        }
        entity.cooldown = 8;

        if (entity.heldItem.isEmpty()) {
            entity.tryExtract(level, pos, state);
        }

        if (!entity.heldItem.isEmpty()) {
            entity.tryTransfer(level, pos, state);
        }
    }

    private void tryExtract(Level level, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            EnumProperty<DuctConnection> prop = getPropertyForFace(dir);
            if (state.getValue(prop) == DuctConnection.EXTRACT) {
                BlockPos neighborPos = pos.relative(dir);
                BlockEntity neighborBE = level.getBlockEntity(neighborPos);

                if (neighborBE instanceof Container neighborInv) {
                    ItemStack extracted = extractItem(neighborInv, dir.getOpposite());
                    if (!extracted.isEmpty()) {
                        this.heldItem = extracted;
                        // We extracted from this side, so don't push back to it!
                        this.lastInputDir = dir;
                        this.setChanged();
                        return;
                    }
                }
            }
        }
    }

    private void tryTransfer(Level level, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            // CHANGE 2: If we received the item from this side, SKIP IT (unless it's the only option, technically)
            // This prevents the "Ping-Pong" effect.
            if (dir == this.lastInputDir) continue;

            EnumProperty<DuctConnection> prop = getPropertyForFace(dir);
            DuctConnection connection = state.getValue(prop);

            if (connection == DuctConnection.NONE || connection == DuctConnection.EXTRACT) continue;

            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);

            // 1. Pipe-to-Pipe Transfer
            if (neighborBE instanceof PhloemDuctBlockEntity targetPipe) {
                if (targetPipe.isEmpty()) {
                    targetPipe.heldItem = this.heldItem.split(1);

                    // CHANGE 3: Tell the target pipe "You got this from [My Side]"
                    // So it doesn't send it back to me next tick.
                    targetPipe.lastInputDir = dir.getOpposite();

                    targetPipe.setChanged();
                    this.setChanged();
                    // Reset our memory since we are now empty
                    this.lastInputDir = null;
                    return;
                }
            }
            // 2. Pipe-to-Inventory Transfer
            else if (neighborBE instanceof Container target) {
                ItemStack remaining = insertItem(target, this.heldItem.copy(), dir.getOpposite());
                if (remaining.getCount() != this.heldItem.getCount()) {
                    this.heldItem = remaining;
                    this.setChanged();
                    // Reset memory on success
                    if (this.heldItem.isEmpty()) this.lastInputDir = null;
                    return;
                }
            }
        }

        // EDGE CASE: Dead End
        // If we skipped the "back" direction but couldn't go anywhere else,
        // we should clear the memory so it CAN go back next tick (instead of getting stuck).
        // (Optional: Only clear if we really tried everything else).
        if (!this.heldItem.isEmpty()) {
            // Simple fallback: if we are still holding the item after a full cycle,
            // allow it to go back next tick.
            this.lastInputDir = null;
        }
    }

    // --- HELPER METHODS ---
    private static ItemStack extractItem(Container source, Direction side) {
        int[] slots;
        if (source instanceof WorldlyContainer worldly) {
            slots = worldly.getSlotsForFace(side);
        } else {
            slots = IntStream.range(0, source.getContainerSize()).toArray();
        }

        for (int i : slots) {
            ItemStack stack = source.getItem(i);
            if (stack.isEmpty()) continue;

            if (source instanceof WorldlyContainer worldly && !worldly.canTakeItemThroughFace(i, stack, side)) {
                continue;
            }

            ItemStack result = stack.split(1);
            source.setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack insertItem(Container destination, ItemStack stack, Direction side) {
        int[] slots;
        if (destination instanceof WorldlyContainer worldly) {
            slots = worldly.getSlotsForFace(side);
        } else {
            slots = IntStream.range(0, destination.getContainerSize()).toArray();
        }

        for (int i : slots) {
            ItemStack slotStack = destination.getItem(i);

            if (!destination.canPlaceItem(i, stack)) continue;
            if (destination instanceof WorldlyContainer worldly && !worldly.canPlaceItemThroughFace(i, stack, side)) {
                continue;
            }

            if (slotStack.isEmpty()) {
                destination.setItem(i, stack);
                destination.setChanged();
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                int toMove = Math.min(space, stack.getCount());

                if (toMove > 0) {
                    slotStack.grow(toMove);
                    stack.shrink(toMove);
                    destination.setChanged();
                    return stack;
                }
            }
        }
        return stack;
    }

    private EnumProperty<DuctConnection> getPropertyForFace(Direction face) {
        return switch (face) {
            case NORTH -> PhloemDuctBlock.NORTH;
            case SOUTH -> PhloemDuctBlock.SOUTH;
            case EAST -> PhloemDuctBlock.EAST;
            case WEST -> PhloemDuctBlock.WEST;
            case UP -> PhloemDuctBlock.UP;
            case DOWN -> PhloemDuctBlock.DOWN;
        };
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("HeldItem", ItemStack.CODEC, heldItem);
        // Optional: Save lastInputDir to persist flow direction on server restart
        // For simplicity, we can skip it (it will just re-figure flow on load)
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.heldItem = input.read("HeldItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    // --- CONTAINER METHODS ---
    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return heldItem.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return heldItem; }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack split = heldItem.split(amount);
        if (heldItem.isEmpty()) heldItem = ItemStack.EMPTY;
        setChanged();
        return split;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = heldItem;
        heldItem = ItemStack.EMPTY;
        return stack;
    }
    @Override public void setItem(int slot, ItemStack stack) { heldItem = stack; setChanged(); }
    @Override public boolean stillValid(Player player) { return false; }
    @Override public void clearContent() { heldItem = ItemStack.EMPTY; setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) { return heldItem.isEmpty(); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) { return true; }
}