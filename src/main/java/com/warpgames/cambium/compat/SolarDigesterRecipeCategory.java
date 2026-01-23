package com.warpgames.cambium.compat;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.recipe.SolarDigesterRecipe;
import com.warpgames.cambium.registry.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class SolarDigesterRecipeCategory implements IRecipeCategory<SolarDigesterRecipe> {

    public static final IRecipeType<SolarDigesterRecipe> RECIPE_TYPE =
            IRecipeType.create(Cambium.MOD_ID, "solar_digesting", SolarDigesterRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public SolarDigesterRecipeCategory(IGuiHelper helper) {
        // 1. Create Background
        this.background = helper.createDrawable(Identifier.fromNamespaceAndPath("cambium", "textures/gui/container/solar_digester.png"), 55, 16, 82, 54);

        // 2. Icon
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SOLAR_DIGESTER));

        // 3. Animated Arrow
        this.arrow = helper.drawableBuilder(Identifier.fromNamespaceAndPath("cambium", "textures/gui/container/solar_digester.png"), 176, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    // --- REQUIRED: Define Size ---
    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    // --- STANDARD METHODS ---
    @Override
    public IRecipeType<SolarDigesterRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Solar Digester");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    // NOTE: getBackground() is REMOVED in JEI 1.21+. We draw it manually below.

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SolarDigesterRecipe recipe, IFocusGroup focuses) {
        // 1. Input Slot
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .add(recipe.getInput());

        // 2. Output Slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19)
                .add(recipe.getOutput());

        // 3. Byproduct Slot
        if (!recipe.getByproduct().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 19)
                    .add(recipe.getByproduct())
                    .addRichTooltipCallback((slotView, tooltip) -> {
                        tooltip.add(Component.literal("Chance: " + (int)(recipe.getByproductChance() * 100) + "%"));
                    });
        }
    }

    @Override
    public void draw(SolarDigesterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // REQUIRED: Draw the background manually now!
        background.draw(guiGraphics, 0, 0);

        // Draw the arrow
        arrow.draw(guiGraphics, 24, 1);
    }
}