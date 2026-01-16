package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
// The Imports from BarrelBlockEntity
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MineralSoilBlockEntity extends BlockEntity implements Container {

    // Size 1, just like we need
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public MineralSoilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINERAL_SOIL_BE, pos, state);
    }

    // --- SAVE LOGIC (Matches BarrelBlockEntity) ---
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        // Barrel uses this exact line:
        ContainerHelper.saveAllItems(output, this.inventory);
    }

    // --- LOAD LOGIC (Matches BarrelBlockEntity) ---
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // Barrel uses this exact line:
        ContainerHelper.loadAllItems(input, this.inventory);
    }

    // --- CONTAINER METHODS ---
    @Override
    public int getContainerSize() { return inventory.size(); }
    @Override
    public boolean isEmpty() { for (ItemStack s : inventory) if (!s.isEmpty()) return true; return false; }
    @Override
    public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override
    public ItemStack removeItem(int slot, int amount) { return ContainerHelper.removeItem(inventory, slot, amount); }
    @Override
    public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        setChanged(); // Required to trigger save
    }
    @Override
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override
    public void clearContent() { inventory.clear(); }
}