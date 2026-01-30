package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.registry.ModBlockEntities;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.screen.SolarConcentratorScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SolarConcentratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, WorldlyContainer {

    // --- INVENTORY CONFIG ---
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int LENS_SLOT = 2;

    // --- DATA SYNC ---
    protected final ContainerData propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72; // Default cook time (faster than furnace's 200)

    public SolarConcentratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_CONCENTRATOR_BE, pos, state);

        // This delegate allows the Screen to read "progress" and "maxProgress" integers
        this.propertyDelegate = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SolarConcentratorBlockEntity.this.progress;
                    case 1 -> SolarConcentratorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SolarConcentratorBlockEntity.this.progress = value;
                    case 1 -> SolarConcentratorBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    // --- THE MAIN LOGIC LOOP ---
    public static void tick(Level level, BlockPos pos, BlockState state, SolarConcentratorBlockEntity entity) {
        if (level.isClientSide) return;

        // 1. CHECK SUNLIGHT
        boolean isDay = level.isDay();
        boolean isRaining = level.isRaining();
        boolean canSeeSky = level.canSeeSky(pos.above());

        // Strict Logic: Must be Day, No Rain, and Sky Visible
        boolean hasSunPower = isDay && !isRaining && canSeeSky;

        // 2. CHECK RECIPE
        if (hasSunPower && hasRecipe(entity)) {
            // Check for Lens in Slot 2
            boolean hasLens = entity.inventory.get(LENS_SLOT).is(ModItems.SOLAR_LENS);

            // Speed Boost: 1 tick per tick usually.
            // If Lens is present, 2 ticks (Double Speed).
            int speed = hasLens ? 2 : 1;

            entity.progress += speed;

            // CRAFT ITEM
            if (entity.progress >= entity.maxProgress) {
                craftItem(entity);
            }
        } else {
            // Cool down if not working
            entity.resetProgress();
        }

        // Optional: Mark dirty if things changed (omitted for brevity)
    }

    private static void craftItem(SolarConcentratorBlockEntity entity) {
        Level level = entity.level;
        SingleRecipeInput input = new SingleRecipeInput(entity.getItem(INPUT_SLOT));

        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, input, level)
                .map(holder -> holder.value());

        if (recipe.isPresent()) {
            ItemStack result = recipe.get().assemble(input, level.registryAccess());
            ItemStack outputStack = entity.getItem(OUTPUT_SLOT);

            if (outputStack.isEmpty()) {
                entity.setItem(OUTPUT_SLOT, result.copy());
            } else if (outputStack.is(result.getItem())) {
                outputStack.grow(result.getCount());
            }

            entity.getItem(INPUT_SLOT).shrink(1);
            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(SolarConcentratorBlockEntity entity) {
        Level level = entity.level;
        SingleRecipeInput input = new SingleRecipeInput(entity.getItem(INPUT_SLOT));

        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, input, level)
                .map(holder -> holder.value());

        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().assemble(input, level.registryAccess());
        ItemStack outputStack = entity.getItem(OUTPUT_SLOT);

        return outputStack.isEmpty() ||
                (outputStack.is(result.getItem()) && outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize());
    }

    private void resetProgress() {
        this.progress = 0;
    }

    // --- SAVING & LOADING ---
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("solar_concentrator.progress", progress);
        output.putInt("solar_concentrator.max_progress", maxProgress);
        ContainerHelper.saveAllItems(output, this.inventory, true);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        // FIX: Handle Optional Return Types
        this.progress = input.getInt("solar_concentrator.progress").orElse(0);
        this.maxProgress = input.getInt("solar_concentrator.max_progress").orElse(100);
        this.inventory.clear();
        ContainerHelper.loadAllItems(input, this.inventory);
    }

    // --- CONTAINER BOILERPLATE ---
    // Standard methods required by the Container interface
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cambium.solar_concentrator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SolarConcentratorScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return ContainerHelper.removeItem(inventory, slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { inventory.clear(); }
    @Override public int[] getSlotsForFace(net.minecraft.core.Direction side) { return new int[]{0, 1, 2}; }
    @Override public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable net.minecraft.core.Direction direction) { return index != OUTPUT_SLOT; }
    @Override public boolean canTakeItemThroughFace(int index, ItemStack stack, net.minecraft.core.Direction direction) { return index == OUTPUT_SLOT; }
}