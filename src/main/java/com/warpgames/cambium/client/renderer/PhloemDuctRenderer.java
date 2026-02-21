package com.warpgames.cambium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.warpgames.cambium.block.entity.PhloemDuctBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// Inner class is 'static' to be visible, but inside the file to satisfy Java rules
public class PhloemDuctRenderer implements BlockEntityRenderer<PhloemDuctBlockEntity, PhloemDuctRenderer.PhloemDuctRenderState> {

    public static class PhloemDuctRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState itemRenderState = new ItemStackRenderState();
        public float time;
        public int light;
        public Direction flowingFrom; // We track where it came from
    }

    private final ItemModelResolver modelResolver;

    public PhloemDuctRenderer(BlockEntityRendererProvider.Context context) {
        this.modelResolver = Minecraft.getInstance().getItemModelResolver();
    }

    @Override
    public PhloemDuctRenderState createRenderState() {
        return new PhloemDuctRenderState();
    }

    public void extractRenderState(PhloemDuctBlockEntity entity, PhloemDuctRenderState state, float tickDelta) {
        ItemStack stack = entity.getItem(0);

        if (stack.isEmpty()) {
            state.itemRenderState.clear();
        } else {
            modelResolver.updateForTopItem(
                    state.itemRenderState,
                    stack,
                    ItemDisplayContext.FIXED,
                    entity.getLevel(),
                    null,
                    0
            );
        }

        if (entity.getLevel() != null) {
            state.time = (entity.getLevel().getGameTime() + tickDelta) * 4;
            state.light = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos());
            // Sync the flow direction
            state.flowingFrom = entity.lastInputDir;
        } else {
            state.time = 0;
            state.light = 15728880;
            state.flowingFrom = null;
        }
    }

    @Override
    public void submit(PhloemDuctRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.itemRenderState.isEmpty()) return;

        poseStack.pushPose();

        // 1. Center the item (0.5, 0.5, 0.5)
        float x = 0.5f;
        float y = 0.5f;
        float z = 0.5f;

        // 2. Apply Flow Offset
        // If we know where it came from, we offset it slightly towards that side
        // creating a "moving in" effect. (You could animate this with 'time' if desired)
        if (state.flowingFrom != null) {
            // Push it 20% towards the source, so it looks like it's arriving
            float offset = -0.2f;
            x += state.flowingFrom.getStepX() * offset;
            y += state.flowingFrom.getStepY() * offset;
            z += state.flowingFrom.getStepZ() * offset;
        }

        poseStack.translate(x, y, z);
        poseStack.scale(0.4f, 0.4f, 0.4f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.time));

        state.itemRenderState.submit(
                poseStack,
                collector,
                state.light,
                OverlayTexture.NO_OVERLAY,
                -1
        );

        poseStack.popPose();
    }
}