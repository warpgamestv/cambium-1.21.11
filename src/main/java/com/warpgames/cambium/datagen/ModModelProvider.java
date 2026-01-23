package com.warpgames.cambium.datagen;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.ResourceFruitBlock;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*; // Import all model classes
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {

    public static final ModelTemplate TINTED_LEAVES = new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath("minecraft", "block/leaves")),
            Optional.empty(),
            TextureSlot.ALL
    );

    public static final ModelTemplate FRUIT_STAGE0 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage0")), Optional.empty(), TextureSlot.ALL);
    public static final ModelTemplate FRUIT_STAGE1 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage1")), Optional.empty(), TextureSlot.ALL);
    public static final ModelTemplate FRUIT_STAGE2 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage2")), Optional.empty(), TextureSlot.ALL);

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createTrivialCube(ModBlocks.ROOT_BLOCK);
        generator.woodProvider(ModBlocks.LIVING_LOG).logWithHorizontal(ModBlocks.LIVING_LOG);

        for (ResourceTree tree : TreeRegistry.TREES) {
            Block leaves = tree.getLeaves();
            Block fruit = tree.getFruit();

            // A. Leaves Block
            Identifier leavesTexture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/living_leaves");
            TextureMapping leavesMapping = new TextureMapping().put(TextureSlot.ALL, leavesTexture);
            Identifier leavesModel = TINTED_LEAVES.create(leaves, leavesMapping, generator.modelOutput);

            generator.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(leaves, BlockModelGenerators.plainVariant(leavesModel))
            );

            // B. Fruit Block
            Identifier rawTexture = Identifier.fromNamespaceAndPath("minecraft", "block/" + tree.getName() + "_ore");
            TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, rawTexture);

            Identifier model0 = FRUIT_STAGE0.createWithSuffix(fruit, "_stage0", mapping, generator.modelOutput);
            Identifier model1 = FRUIT_STAGE1.createWithSuffix(fruit, "_stage1", mapping, generator.modelOutput);
            Identifier model2 = FRUIT_STAGE2.createWithSuffix(fruit, "_stage2", mapping, generator.modelOutput);

            generator.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(fruit)
                            .with(PropertyDispatch.initial(ResourceFruitBlock.AGE)
                                    .select(0, BlockModelGenerators.plainVariant(model0))
                                    .select(1, BlockModelGenerators.plainVariant(model1))
                                    .select(2, BlockModelGenerators.plainVariant(model2))
                            )
            );
        }
    }

    // --- FIXED ITEM MODELS ---
    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModBlocks.ROOT_BLOCK.asItem(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModBlocks.MINERAL_SOIL.asItem(), ModelTemplates.FLAT_ITEM);
        // 1. Define the correct texture location (block/living_leaves)
        Identifier leafTexture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/living_leaves");

        for (ResourceTree tree : TreeRegistry.TREES) {
            Item leafItem = tree.getLeaves().asItem();

            // 2. Generate the model manually
            // We map "layer0" (the standard item layer) to our block texture.
            ModelTemplates.FLAT_ITEM.create(
                    ModelLocationUtils.getModelLocation(leafItem),
                    TextureMapping.layer0(leafTexture),
                    itemModelGenerator.modelOutput
            );
            Identifier fruitTexture = Identifier.fromNamespaceAndPath("minecraft", "block/" + tree.getName() + "_ore");
            ModelTemplates.FLAT_ITEM.create(
                    ModelLocationUtils.getModelLocation(tree.getFruit().asItem()),
                    TextureMapping.layer0(fruitTexture),
                    itemModelGenerator.modelOutput
            );
        }
    }
}