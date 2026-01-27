package com.warpgames.cambium.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
// You might need to check if ClientRecipeContainer is importable.
// If it's package-private (no 'public' modifier), you might need to use Object or RecipeAccess.
import net.minecraft.client.multiplayer.ClientRecipeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPacketListener.class)
public interface ClientPacketListenerAccessor {
    @Accessor("recipes")
    ClientRecipeContainer getRecipes();
}