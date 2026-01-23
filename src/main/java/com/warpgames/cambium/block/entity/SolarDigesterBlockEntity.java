package com.warpgames.cambium.block.entity;

import com.mojang.serialization.Codec;
import com.warpgames.cambium.menu.SolarDigesterMenu;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper; // Required for saveAllItems
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class SolarDigesterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, Container {

    // Variable is named 'inventory', NOT 'items'
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);

    private int progress = 0;
    private int maxProgress = 100;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> SolarDigesterBlockEntity.this.progress;
                case 1 -> SolarDigesterBlockEntity.this.maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> SolarDigesterBlockEntity.this.progress = value;
                case 1 -> SolarDigesterBlockEntity.this.maxProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public SolarDigesterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SOLAR_DIGESTER_BE, pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolarDigesterBlockEntity entity) {
        if (level.isClientSide()) return;

        boolean isDay = level.getDayTime() % 24000 < 12300;
        boolean canSeeSky = level.canSeeSky(pos.above());

        ItemStack input = entity.inventory.get(0);
        ItemStack output = entity.inventory.get(1);

        boolean hasRecipe = !input.isEmpty() && input.is(Items.COBBLESTONE);

        if (isDay && canSeeSky && hasRecipe) {
            boolean outputValid = output.isEmpty() || (output.is(Items.STONE) && output.getCount() < output.getMaxStackSize());

            if (outputValid) {
                entity.progress++;
                if (entity.progress >= entity.maxProgress) {
                    entity.progress = 0;
                    input.shrink(1);
                    if (output.isEmpty()) {
                        entity.inventory.set(1, new ItemStack(Items.STONE));
                    } else {
                        output.grow(1);
                    }
                    entity.setChanged();
                }
            }
        } else {
            if (entity.progress > 0) {
                entity.progress = 0;
                entity.setChanged();
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SolarDigesterMenu(syncId, playerInventory, ContainerLevelAccess.create(level, this.getBlockPos()), this.data, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Solar Digester");
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.getBlockPos();
    }

    // --- CONTAINER METHODS ---
    @Override
    public int getContainerSize() { return inventory.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) { if (!stack.isEmpty()) return false; }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) { return inventory.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(inventory, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }

    @Override
    public void clearContent() { inventory.clear(); }

    // --- SAVING AND LOADING ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.inventory);

        output.store("Progress", Codec.INT, this.progress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.inventory.clear();
        ContainerHelper.loadAllItems(input, this.inventory);

        input.read("Progress", Codec.INT).ifPresent(p -> this.progress = p);
    }
}