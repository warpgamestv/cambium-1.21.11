package com.warpgames.cambium.datagen;

import com.warpgames.cambium.recipe.SolarConcentratorRecipe;
import com.warpgames.cambium.recipe.SolarDigesterRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SolarConcentratorRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final ItemStack output;
    private int cookingTime = 100;
    private float experience = 0.0f;
    private boolean requiresLens = false;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private SolarConcentratorRecipeBuilder(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    // --- STATIC STARTERS ---

    // 1. Generic version
    public static SolarConcentratorRecipeBuilder concentrator(Ingredient input, ItemLike output, int count) {
        return new SolarConcentratorRecipeBuilder(input, new ItemStack(output, count));
    }

    // 2. Simple Item version
    public static SolarConcentratorRecipeBuilder concentrator(ItemLike input, ItemLike output, int count) {
        return concentrator(Ingredient.of(input), output, count);
    }

    // --- CHAINABLE METHODS ---
    public SolarConcentratorRecipeBuilder requiresLens() {
        this.requiresLens = true;
        return this;
    }

    public SolarConcentratorRecipeBuilder time(int ticks) {
        this.cookingTime = ticks;
        return this;
    }

    public SolarConcentratorRecipeBuilder xp(float xp) {
        this.experience = xp;
        return this;
    }

    @Override
    public SolarConcentratorRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public SolarConcentratorRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> resourceKey) {
        this.ensureValid(resourceKey);

        Advancement.Builder builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(builder::addCriterion);

        SolarConcentratorRecipe recipe = new SolarConcentratorRecipe(
                this.input,
                this.output,
                this.cookingTime,
                this.experience,
                this.requiresLens
        );

        output.accept(
                resourceKey,
                recipe,
                builder.build(resourceKey.identifier().withPrefix("recipes/solar_concentrating/"))
        );
    }

    @Override
    public void save(RecipeOutput output) {
        Identifier itemId = RecipeBuilder.getDefaultRecipeId(this.getResult());
        Identifier recipeId = Identifier.fromNamespaceAndPath(itemId.getNamespace(), "solar_concentrating/" + itemId.getPath());
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, recipeId);
        save(output, key);
    }

    private void ensureValid(ResourceKey<Recipe<?>> resourceKey) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceKey.identifier());
        }
    }
}