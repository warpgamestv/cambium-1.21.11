package com.warpgames.cambium.datagen;

import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;



import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        valueLookupBuilder(ModTags.Items.LENS)
                .add(ModItems.SOLAR_LENS);

        valueLookupBuilder(ItemTags.LOGS)
                .add(ModBlocks.LIVING_LOG.asItem());

        valueLookupBuilder(ModTags.Items.DIGESTABLE)
                .forceAddTag(ItemTags.LEAVES)
                .forceAddTag(ItemTags.SAPLINGS)
                .forceAddTag(ItemTags.FLOWERS)
                .forceAddTag(ItemTags.PLANKS);
    }

}