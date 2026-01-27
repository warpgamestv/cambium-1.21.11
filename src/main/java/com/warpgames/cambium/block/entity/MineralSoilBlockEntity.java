package com.warpgames.cambium.block.entity;

import com.mojang.serialization.Codec;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class MineralSoilBlockEntity extends BlockEntity {

    private int charge = 0;
    public static final int MAX_CHARGE = 100;

    public MineralSoilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINERAL_SOIL_BE, pos, state);
    }

    // --- LOGIC: CALLED BY LEAVES ---
    // Returns true if successful (charge was consumed)
    public boolean tryConsumeCharge(int amount) {
        if (charge >= amount) {
            charge -= amount;
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return true;
        }
        return false;
    }

    // --- LOGIC: CALLED BY PLAYER ---
    public void addCharge(int amount) {
        this.charge = Math.min(charge + amount, MAX_CHARGE);
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // --- SAVING & LOADING (NEW SYSTEM) ---
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("Charge", Codec.INT, this.charge);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.charge = input.read("Charge", Codec.INT).orElse(0);
    }

    // --- SYNCING (For visual effects later) ---
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}