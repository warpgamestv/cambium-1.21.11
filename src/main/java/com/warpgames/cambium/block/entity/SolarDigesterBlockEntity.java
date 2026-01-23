package com.warpgames.cambium.block.entity;

import com.warpgames.cambium.menu.SolarDigesterMenu;
import com.warpgames.cambium.recipe.ModRecipes;
import com.warpgames.cambium.recipe.SolarDigesterRecipe;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SolarDigesterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, Container {

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int BYPRODUCT_SLOT = 2;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public SolarDigesterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SOLAR_DIGESTER_BE, pos, blockState);
        this.data = new ContainerData() {
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
            public int getCount() { return 2; }
        };
    }

    // --- TICK LOGIC ---
    public static void tick(Level level, BlockPos pos, BlockState state, SolarDigesterBlockEntity entity) {
        if (level.isClientSide()) return;

        boolean isDay = level.getDayTime() % 24000 < 12300;
        boolean canSeeSky = level.canSeeSky(pos.above());

        if (!isDay || !canSeeSky) return;

        if (entity.inventory.get(INPUT_SLOT).isEmpty()) {
            entity.resetProgress();
            return;
        }

        Optional<RecipeHolder<SolarDigesterRecipe>> recipe = entity.getRecipe();

        if (recipe.isPresent()) {
            SolarDigesterRecipe validRecipe = recipe.get().value();
            entity.maxProgress = validRecipe.getCookingTime();

            if (entity.canCraft(validRecipe)) {
                entity.progress++;
                if (entity.progress >= entity.maxProgress) {
                    entity.craftItem(validRecipe);
                    entity.resetProgress();
                }
                setChanged(level, pos, state);
            } else {
                entity.resetProgress();
            }
        } else {
            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    // --- HELPER: FIND RECIPE ---
    private Optional<RecipeHolder<SolarDigesterRecipe>> getRecipe() {
        if (this.level == null) return Optional.empty();

        if (this.level instanceof ServerLevel serverLevel) {
            SingleRecipeInput input = new SingleRecipeInput(this.inventory.get(INPUT_SLOT));
            return serverLevel.recipeAccess().getRecipeFor(ModRecipes.SOLAR_DIGESTER_TYPE, input, this.level);
        }
        return Optional.empty();
    }

    // --- HELPER: CHECK IF OUTPUT FITS ---
    private boolean canCraft(SolarDigesterRecipe recipe) {
        ItemStack recipeOutput = recipe.getOutput();
        ItemStack recipeByproduct = recipe.getByproduct();
        ItemStack currentOutput = this.inventory.get(OUTPUT_SLOT);
        ItemStack currentByproduct = this.inventory.get(BYPRODUCT_SLOT);

        boolean outputFits = currentOutput.isEmpty() ||
                (currentOutput.getItem() == recipeOutput.getItem() &&
                        currentOutput.getCount() + recipeOutput.getCount() <= currentOutput.getMaxStackSize());

        boolean byproductFits = recipeByproduct.isEmpty() ||
                currentByproduct.isEmpty() ||
                (currentByproduct.getItem() == recipeByproduct.getItem() &&
                        currentByproduct.getCount() + recipeByproduct.getCount() <= currentByproduct.getMaxStackSize());

        return outputFits && byproductFits;
    }

    private void craftItem(SolarDigesterRecipe recipe) {
        this.removeItem(INPUT_SLOT, 1);

        ItemStack newOutput = recipe.getOutput();
        if (this.inventory.get(OUTPUT_SLOT).isEmpty()) {
            this.setItem(OUTPUT_SLOT, newOutput.copy());
        } else {
            this.inventory.get(OUTPUT_SLOT).grow(newOutput.getCount());
        }

        if (!recipe.getByproduct().isEmpty()) {
            if (this.level.random.nextFloat() < recipe.getByproductChance()) {
                ItemStack newByproduct = recipe.getByproduct();
                if (this.inventory.get(BYPRODUCT_SLOT).isEmpty()) {
                    this.setItem(BYPRODUCT_SLOT, newByproduct.copy());
                } else {
                    this.inventory.get(BYPRODUCT_SLOT).grow(newByproduct.getCount());
                }
            }
        }
    }

    // --- SCREEN FACTORY ---
    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.getBlockPos();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Solar Digester");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new SolarDigesterMenu(syncId, playerInventory, ContainerLevelAccess.create(level, this.getBlockPos()), this.data, this);
    }

    // --- CONTAINER METHODS ---
    @Override
    public int getContainerSize() { return inventory.size(); }
    @Override
    public boolean isEmpty() { for (ItemStack stack : inventory) if (!stack.isEmpty()) return false; return true; }
    @Override
    public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override
    public ItemStack removeItem(int slot, int amount) { ItemStack r = ContainerHelper.removeItem(inventory, slot, amount); if (!r.isEmpty()) setChanged(); return r; }
    @Override
    public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override
    public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override
    public void clearContent() { inventory.clear(); }

    // --- SAVING AND LOADING (FIXED FOR 1.21.4) ---

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("solar_digester.progress", progress);
        output.putInt("solar_digester.max_progress", maxProgress);

        // FIX: Direct save to ValueOutput (No CompoundTag wrapper needed)
        // Note: Use 'true' if your version requires a boolean, or omit if it only takes (output, inventory)
        ContainerHelper.saveAllItems(output, this.inventory, true);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        // FIX: Handle Optional Return Types
        this.progress = input.getInt("solar_digester.progress").orElse(0);
        this.maxProgress = input.getInt("solar_digester.max_progress").orElse(100);

        // FIX: Direct load from ValueInput
        this.inventory.clear();
        ContainerHelper.loadAllItems(input, this.inventory);
    }
}