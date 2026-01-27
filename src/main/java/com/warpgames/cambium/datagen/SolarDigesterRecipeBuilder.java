package com.warpgames.cambium.datagen;

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

public class SolarDigesterRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final ItemStack output;
    private ItemStack byproduct = ItemStack.EMPTY;
    private float byproductChance = 0.0f;
    private int cookingTime = 100;
    private float experience = 0.0f;
    private boolean requiresLens = false;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    private SolarDigesterRecipeBuilder(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    // --- STATIC STARTERS ---

    // 1. Generic version
    public static SolarDigesterRecipeBuilder digester(Ingredient input, ItemLike output, int count) {
        return new SolarDigesterRecipeBuilder(input, new ItemStack(output, count));
    }

    // 2. Simple Item version
    public static SolarDigesterRecipeBuilder digester(ItemLike input, ItemLike output, int count) {
        return digester(Ingredient.of(input), output, count);
    }

    // --- CHAINABLE METHODS ---
    public SolarDigesterRecipeBuilder byproduct(ItemLike item, int count, float chance) {
        this.byproduct = new ItemStack(item, count);
        this.byproductChance = chance;
        return this;
    }
    public SolarDigesterRecipeBuilder requiresLens() {
        this.requiresLens = true;
        return this;
    }

    public SolarDigesterRecipeBuilder time(int ticks) {
        this.cookingTime = ticks;
        return this;
    }

    public SolarDigesterRecipeBuilder xp(float xp) {
        this.experience = xp;
        return this;
    }

    @Override
    public SolarDigesterRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public SolarDigesterRecipeBuilder group(@Nullable String group) {
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

        SolarDigesterRecipe recipe = new SolarDigesterRecipe(
                this.input,
                this.output,
                this.byproduct,
                this.byproductChance,
                this.cookingTime,
                this.experience,
                this.requiresLens
        );

        output.accept(
                resourceKey,
                recipe,
                builder.build(resourceKey.identifier().withPrefix("recipes/solar_digesting/"))
        );
    }

    @Override
    public void save(RecipeOutput output) {
        Identifier itemId = RecipeBuilder.getDefaultRecipeId(this.getResult());
        Identifier recipeId = Identifier.fromNamespaceAndPath(itemId.getNamespace(), "solar_digesting/" + itemId.getPath());
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, recipeId);
        save(output, key);
    }

    private void ensureValid(ResourceKey<Recipe<?>> resourceKey) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceKey.identifier());
        }
    }
}