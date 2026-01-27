package com.warpgames.cambium.client.screen;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.menu.SolarDigesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SolarDigesterScreen extends AbstractContainerScreen<SolarDigesterMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "textures/gui/solar_digester.png");

    public SolarDigesterScreen(SolarDigesterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

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
    }
}