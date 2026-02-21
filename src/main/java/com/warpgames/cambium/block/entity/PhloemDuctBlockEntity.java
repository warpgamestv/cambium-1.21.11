package com.warpgames.cambium.block.entity;

import com.mojang.serialization.Codec; // Required for Codec.INT
import com.warpgames.cambium.block.transport.DuctConnection;
import com.warpgames.cambium.block.transport.PhloemDuctBlock;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class PhloemDuctBlockEntity extends BlockEntity implements WorldlyContainer {

    private ItemStack heldItem = ItemStack.EMPTY;
    public int cooldown = 0;
    public Direction lastInputDir = null;

    public PhloemDuctBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PHLOEM_DUCT, pos, blockState);
    }

    // --- TICK HANDLER ---
    public static void tick(Level level, BlockPos pos, BlockState state, PhloemDuctBlockEntity entity) {
        if (level.isClientSide()) {
            entity.clientTick();
        } else {
            entity.serverTick(level, pos, state);
        }
    }

    private void clientTick() {
        if (this.cooldown > 0) {
            this.cooldown--;
        }
    }

    private void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }
        this.cooldown = 8;

        if (this.heldItem.isEmpty()) {
            this.tryExtract(level, pos, state);
        }

        if (!this.heldItem.isEmpty()) {
            this.tryTransfer(level, pos, state);
        }
    }

    // --- LOGIC ---
    private void tryExtract(Level level, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            if (state.getValue(getPropertyForFace(dir)) == DuctConnection.EXTRACT) {
                BlockEntity neighborBE = level.getBlockEntity(pos.relative(dir));
                if (neighborBE instanceof Container neighborInv) {
                    ItemStack extracted = extractItem(neighborInv, dir.getOpposite());
                    if (!extracted.isEmpty()) {
                        this.heldItem = extracted;
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
            if (dir == this.lastInputDir) continue;

            DuctConnection connection = state.getValue(getPropertyForFace(dir));
            if (connection == DuctConnection.NONE || connection == DuctConnection.EXTRACT) continue;

            BlockEntity neighborBE = level.getBlockEntity(pos.relative(dir));

            if (neighborBE instanceof PhloemDuctBlockEntity targetPipe) {
                if (targetPipe.isEmpty()) {
                    targetPipe.heldItem = this.heldItem.split(1);
                    targetPipe.lastInputDir = dir.getOpposite();
                    targetPipe.cooldown = 8;
                    targetPipe.setChanged();

                    this.setChanged();
                    if (this.heldItem.isEmpty()) this.lastInputDir = null;
                    return;
                }
            } else if (neighborBE instanceof Container target) {
                ItemStack remaining = insertItem(target, this.heldItem.copy(), dir.getOpposite());
                if (remaining.getCount() != this.heldItem.getCount()) {
                    this.heldItem = remaining;
                    this.setChanged();
                    if (this.heldItem.isEmpty()) this.lastInputDir = null;
                    return;
                }
            }
        }
    }

    // --- SAVING / LOADING (UPDATED) ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        // 1. Save Item
        output.store("HeldItem", ItemStack.CODEC, heldItem);

        // 2. Save Cooldown (Essential for animation resume on load)
        output.store("Cooldown", Codec.INT, cooldown);

        // 3. Save Direction (Essential for knowing where the item "came from")
        if (lastInputDir != null) {
            output.store("LastInputDir", Direction.CODEC, lastInputDir);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // 1. Load Item
        this.heldItem = input.read("HeldItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);

        // 2. Load Cooldown
        this.cooldown = input.read("Cooldown", Codec.INT).orElse(0);

        // 3. Load Direction
        this.lastInputDir = input.read("LastInputDir", Direction.CODEC).orElse(null);
    }

    // --- SYNCING ---
    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        // FIX: Use Codec instead of .save()
        if (!heldItem.isEmpty()) {
            Tag itemTag = ItemStack.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), heldItem).getOrThrow();
            tag.put("HeldItem", itemTag);
        }

        if (lastInputDir != null) {
            tag.putInt("LastInputDir", lastInputDir.get3DDataValue());
        }

        tag.putInt("Cooldown", cooldown);
        return tag;
    }

    // --- HELPER METHODS ---
    private static ItemStack extractItem(Container source, Direction side) {
        if (source.isEmpty()) return ItemStack.EMPTY;
        int[] slots = (source instanceof WorldlyContainer w) ? w.getSlotsForFace(side) : IntStream.range(0, source.getContainerSize()).toArray();
        for (int i : slots) {
            ItemStack stack = source.getItem(i);
            if (!stack.isEmpty() && (!(source instanceof WorldlyContainer w) || w.canTakeItemThroughFace(i, stack, side))) {
                return source.removeItem(i, 1);
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack insertItem(Container destination, ItemStack stack, Direction side) {
        if (stack.isEmpty()) return stack;
        int[] slots = (destination instanceof WorldlyContainer w) ? w.getSlotsForFace(side) : IntStream.range(0, destination.getContainerSize()).toArray();
        for (int i : slots) {
            ItemStack slotStack = destination.getItem(i);
            if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int limit = Math.min(destination.getMaxStackSize(stack), slotStack.getMaxStackSize());
                int space = limit - slotStack.getCount();
                if (space > 0) {
                    int move = Math.min(space, stack.getCount());
                    slotStack.grow(move);
                    stack.shrink(move);
                    destination.setChanged();
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        for (int i : slots) {
            if (destination.getItem(i).isEmpty()) {
                destination.setItem(i, stack.copy());
                stack.setCount(0);
                destination.setChanged();
                return ItemStack.EMPTY;
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

    // --- CONTAINER IMPL ---
    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return heldItem.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return heldItem; }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack s = heldItem.split(amount); setChanged(); return s; }
    @Override public ItemStack removeItemNoUpdate(int slot) { ItemStack s = heldItem; heldItem = ItemStack.EMPTY; return s; }
    @Override public void setItem(int slot, ItemStack stack) { heldItem = stack; setChanged(); }
    @Override public boolean stillValid(Player player) { return false; }
    @Override public void clearContent() { heldItem = ItemStack.EMPTY; setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) { return heldItem.isEmpty(); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) { return true; }
}