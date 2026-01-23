package com.warpgames.cambium.block.entity;

import com.mojang.serialization.Codec;
import com.warpgames.cambium.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MineralSoilBlockEntity extends BlockEntity {

    private int charge = 0;
    public static final int MAX_CHARGE = 100;

    public MineralSoilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MINERAL_SOIL_BE, pos, state);
    }

    // --- LOGIC: CALLED BY LEAVES ---
    // Returns true if successful (charge was consumed)
    public boolean tryConsumeCharge() {
        if (this.charge > 0) {
            this.charge--;
            setChanged(); // Mark as dirty so it saves
            return true;
        }
        return false;
    }

    // --- LOGIC: CALLED BY PLAYER ---
    public boolean addCharge(int amount) {
        if (this.charge < MAX_CHARGE) {
            this.charge = Math.min(this.charge + amount, MAX_CHARGE);
            setChanged();
            return true;
        }
        return false;
    }

    // --- GETTER (Optional: For Debugging/Waila) ---
    public int getCharge() {
        return charge;
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
}