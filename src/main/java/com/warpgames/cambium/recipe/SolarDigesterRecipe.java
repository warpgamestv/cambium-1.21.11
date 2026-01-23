package com.warpgames.cambium.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.warpgames.cambium.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;

public class SolarDigesterRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final ItemStack byproduct;
    private final float byproductChance;
    private final int cookingTime;
    private final float experience;
    private final String group;

    public SolarDigesterRecipe(Ingredient input, ItemStack output, ItemStack byproduct, float byproductChance, int cookingTime, float experience) {
        this.input = input;
        this.output = output;
        this.byproduct = byproduct;
        this.byproductChance = byproductChance;
        this.cookingTime = cookingTime;
        this.experience = experience;
        this.group = "";
    }

    // --- GETTERS ---
    public Ingredient getInput() { return input; }
    public ItemStack getOutput() { return output.copy(); }
    public ItemStack getByproduct() { return byproduct.copy(); }
    public float getByproductChance() { return byproductChance; }
    public int getCookingTime() { return cookingTime; }
    public float getExperience() { return experience; }

    // --- STANDARD LOGIC ---

    @Override
    public boolean matches(SingleRecipeInput inv, Level level) {
        return this.input.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput inv, HolderLookup.Provider lookup) {
        return output.copy();
    }

    // --- FIX: Deleted 'canCraftInDimensions' and 'getResultItem' (Provider) as they no longer exist in your version ---

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.input);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_BLOCKS;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new FurnaceRecipeDisplay(
                        this.input.display(),
                        SlotDisplay.AnyFuel.INSTANCE,
                        new SlotDisplay.ItemStackSlotDisplay(this.output),
                        new SlotDisplay.ItemSlotDisplay(ModBlocks.SOLAR_DIGESTER.asItem()),
                        this.cookingTime,
                        this.experience
                )
        );
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipes.SOLAR_DIGESTER_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipes.SOLAR_DIGESTER_TYPE;
    }

    // --- THE SERIALIZER ---
    public static class Serializer implements RecipeSerializer<SolarDigesterRecipe> {
        public static final MapCodec<SolarDigesterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(SolarDigesterRecipe::getInput),
                ItemStack.CODEC.fieldOf("result").forGetter(SolarDigesterRecipe::getOutput),
                ItemStack.CODEC.optionalFieldOf("byproduct", ItemStack.EMPTY).forGetter(SolarDigesterRecipe::getByproduct),
                Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(SolarDigesterRecipe::getByproductChance),
                Codec.INT.optionalFieldOf("cookingtime", 100).forGetter(SolarDigesterRecipe::getCookingTime),
                Codec.FLOAT.optionalFieldOf("experience", 0.0f).forGetter(SolarDigesterRecipe::getExperience)
        ).apply(inst, SolarDigesterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SolarDigesterRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SolarDigesterRecipe::getInput,
                ItemStack.STREAM_CODEC, SolarDigesterRecipe::getOutput,
                ItemStack.STREAM_CODEC, SolarDigesterRecipe::getByproduct,
                net.minecraft.network.codec.ByteBufCodecs.FLOAT, SolarDigesterRecipe::getByproductChance,
                net.minecraft.network.codec.ByteBufCodecs.INT, SolarDigesterRecipe::getCookingTime,
                net.minecraft.network.codec.ByteBufCodecs.FLOAT, SolarDigesterRecipe::getExperience,
                SolarDigesterRecipe::new
        );

        @Override
        public MapCodec<SolarDigesterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SolarDigesterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}