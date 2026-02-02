package com.warpgames.cambium.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.menu.SolarConcentratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SolarConcentratorScreen extends AbstractContainerScreen<SolarConcentratorMenu> {
    // Defines where the game looks for your GUI texture
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "textures/gui/solar_concentrator.png");

    public SolarConcentratorScreen(SolarConcentratorMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // You can remove the title label if you want the GUI to look cleaner
        // this.titleLabelX = 10000;
        this.inventoryLabelY = 72; // Adjusts where "Inventory" text appears
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

// ARGUMENTS EXPLAINED:
        // 1. Pipeline:      RenderPipelines.GUI_TEXTURED (Standard GUI drawing)
        // 2. Texture:       TEXTURE
        // 3. X, Y:          x, y
        // 4. U, V:          0.0F, 0.0F  (Start of texture)
        // 5. Draw Size:     this.imageWidth, this.imageHeight (Size on screen)
        // 6. Texture Slice: this.imageWidth, this.imageHeight (Size of texture chunk to use)
        // 7. File Size:     256, 256    (Total size of png)
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x, y,
                0.0F, 0.0F,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight,
                256, 256
        );

        // 2. Draw the Progress Arrow
        int arrowWidth = this.menu.getScaledProgress();

        if (arrowWidth > 0) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, // 1. Pipeline
                    TEXTURE,                      // 2. Texture
                    x + 79, y + 42,               // 3. Screen X, Y
                    176.0F, 14.0F,                // 4. Texture U, V (Start of white arrow)
                    arrowWidth, 17,               // 5. Width (Dynamic), Height (Fixed)
                    arrowWidth, 17,               // 6. Slice Width/Height (Same as above)
                    256, 256                      // 7. Texture File Size
            );
        }

        // 3. (Optional) Draw a "Sun" icon if it is active
        // You could add logic here to light up a sun icon if the machine sees the sky
    }
}