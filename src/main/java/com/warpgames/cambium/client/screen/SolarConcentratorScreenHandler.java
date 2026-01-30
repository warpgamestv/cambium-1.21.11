package com.warpgames.cambium.client.screen;

import com.warpgames.cambium.registry.ModScreenHandlers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class SolarConcentratorScreenHandler extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData propertyDelegate;

    // Client-side constructor
    public SolarConcentratorScreenHandler(int syncId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(2));
    }

    // Server-side constructor
    public SolarConcentratorScreenHandler(int syncId, Inventory playerInventory, Container container, ContainerData delegate) {
        super(ModScreenHandlers.SOLAR_CONCENTRATOR_SCREEN_HANDLER, syncId);
        checkContainerSize(container, 3);
        this.container = container;
        this.propertyDelegate = delegate;

        container.startOpen(playerInventory.player);

        // --- MACHINE SLOTS ---
        // Slot 0: Input
        this.addSlot(new Slot(container, 0, 56, 17));
        // Slot 1: Output
        this.addSlot(new Slot(container, 1, 116, 35) {
            @Override public boolean mayPlace(ItemStack stack) { return false; } // Prevents players putting items in output
        });
        // Slot 2: Lens Upgrade
        this.addSlot(new Slot(container, 2, 56, 53));

        // --- PLAYER INVENTORY SLOTS ---
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        // Syncs the progress integers
        this.addDataSlots(delegate);
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = propertyDelegate.get(0);
        int maxProgress = propertyDelegate.get(1);  // Max Progress
        int progressArrowSize = 24; // This is the width of your arrow sprite in pixels

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < 3) {
                if (!this.moveItemStackTo(originalStack, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}