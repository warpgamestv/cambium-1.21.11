package com.warpgames.cambium.compat.jei;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.recipe.SolarDigesterRecipe;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems; // Ensure this is imported for the Lens
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class SolarDigesterCategory implements IRecipeCategory<SolarDigesterRecipe> {

    public static final Identifier UID = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digester");
    public static final IRecipeType<SolarDigesterRecipe> RECIPE_TYPE =
            IRecipeType.create(Cambium.MOD_ID, "solar_digester", SolarDigesterRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public SolarDigesterCategory(IGuiHelper helper) {
        Identifier texture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "textures/gui/solar_digester.png");

        // 1. Background (Crop)
        // x=38, y=10, w=106, h=64
        this.background = helper.createDrawable(texture, 38, 10, 106, 64);

        // 2. Icon
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.SOLAR_DIGESTER));

        // 3. Slot Background (Standard JEI dark box)
        this.slotDrawable = helper.getSlotDrawable();
    }

    @Override
    public IRecipeType<SolarDigesterRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public Component getTitle() {
        return Component.literal("Solar Digester");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SolarDigesterRecipe recipe, IFocusGroup focuses) {

        var lensSlot = builder.addSlot(RecipeIngredientRole.INPUT, 44, 8)
                .setBackground(slotDrawable, -1, -1);
        if (recipe.requiresLens()) {
            lensSlot.add(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.SOLAR_LENS));
            lensSlot.addRichTooltipCallback((view, list) -> {
                list.add(Component.literal("Required").withStyle(ChatFormatting.RED));
            });
        }

        // --- 2. INPUT SLOT (Left) ---
        // GUI Pos: 44, 36 (Approx)
        // Relative: 6, 26
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 26)
                .setBackground(slotDrawable, -1, -1)
                .add(recipe.getInput());

        // --- 3. MAIN OUTPUT (Top Right) ---
        // GUI Pos: 116, 26
        // Relative: 78, 16
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 16)
                .setBackground(slotDrawable, -1, -1)
                .add(recipe.getOutput());

        // --- 4. BYPRODUCT (Bottom Right) ---
        // GUI Pos: 116, 48
        // Relative: 78, 38
        if (!recipe.getByproduct().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 38)
                    .setBackground(slotDrawable, -1, -1)
                    .add(recipe.getByproduct());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SolarDigesterRecipe recipe, IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(recipe.getCookingTime())
                .setPosition(38, 26);
    }

    @Override
    public void draw(SolarDigesterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        float xp = recipe.getExperience();
        if (xp > 0) {
            String xpString = String.format("%.1f XP", xp);
            int stringWidth = font.width(xpString);
            guiGraphics.drawString(font, xpString, 87 - (stringWidth / 2), 5, 0x808080, false);
        }

        int cookTime = recipe.getCookingTime();
        if (cookTime > 0) {
            int seconds = cookTime / 20;
            String timeString = seconds + "s";
            int stringWidth = font.width(timeString);
            guiGraphics.drawString(font, timeString, 87 - (stringWidth / 2), 56, 0x808080, false);
        }
    }
}