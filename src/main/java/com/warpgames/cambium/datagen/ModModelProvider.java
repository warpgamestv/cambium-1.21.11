package com.warpgames.cambium.datagen;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.block.ResourceFruitBlock;
import com.warpgames.cambium.content.ResourceTree;
import com.warpgames.cambium.registry.ModBlocks;
import com.warpgames.cambium.registry.ModItems;
import com.warpgames.cambium.registry.TreeRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {

    public static final TextureSlot STEM_SLOT = TextureSlot.create("stem");
    public static final TextureSlot LEAVES_SLOT = TextureSlot.create("leaves");

    public static final ModelTemplate TINTED_LEAVES = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath("minecraft", "block/leaves")), Optional.empty(), TextureSlot.ALL);
    public static final ModelTemplate TINTED_CROSS_OVERLAY = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/tinted_cross_overlay")), Optional.empty(), STEM_SLOT, LEAVES_SLOT);
    public static final ModelTemplate FRUIT_STAGE0 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage0")), Optional.empty(), TextureSlot.ALL);
    public static final ModelTemplate FRUIT_STAGE1 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage1")), Optional.empty(), TextureSlot.ALL);
    public static final ModelTemplate FRUIT_STAGE2 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/base_fruit_stage2")), Optional.empty(), TextureSlot.ALL);

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {

        // --- 1. ROOT BLOCK (Fixed to be a Column) ---
        // Instead of a trivial cube, we use CUBE_COLUMN to separate Top/Bottom from Sides.
        Identifier rootSide = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/root_block");
        Identifier rootTop = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/root_block_top");

        Identifier rootModel = ModelTemplates.CUBE_COLUMN.create(
                ModBlocks.ROOT_BLOCK,
                TextureMapping.column(rootSide, rootTop),
                generator.modelOutput
        );
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.ROOT_BLOCK, BlockModelGenerators.plainVariant(rootModel)));

        // --- 2. MINERAL SOIL ---
        // Keeps the trivial cube logic (same texture on all sides)
        generator.createTrivialCube(ModBlocks.MINERAL_SOIL);

        // --- 3. LOGS ---
        generator.woodProvider(ModBlocks.LIVING_LOG).logWithHorizontal(ModBlocks.LIVING_LOG);

        // --- 4. DYNAMIC TREES ---
        for (ResourceTree tree : TreeRegistry.TREES) {
            Block leaves = tree.getLeaves();
            Block fruit = tree.getFruit();
            Block sapling = tree.getSapling();

            // A. LEAVES
            Identifier leavesTexture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/living_leaves");
            TextureMapping leavesMapping = new TextureMapping().put(TextureSlot.ALL, leavesTexture);
            Identifier leavesModel = TINTED_LEAVES.create(leaves, leavesMapping, generator.modelOutput);
            generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(leaves, BlockModelGenerators.plainVariant(leavesModel)));

            // B. FRUIT
            Identifier rawTexture = Identifier.fromNamespaceAndPath(tree.getModId(), "block/" + tree.getName() + "_ore");
            TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, rawTexture);
            Identifier model0 = FRUIT_STAGE0.createWithSuffix(fruit, "_stage0", mapping, generator.modelOutput);
            Identifier model1 = FRUIT_STAGE1.createWithSuffix(fruit, "_stage1", mapping, generator.modelOutput);
            Identifier model2 = FRUIT_STAGE2.createWithSuffix(fruit, "_stage2", mapping, generator.modelOutput);
            generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(fruit)
                    .with(PropertyDispatch.initial(ResourceFruitBlock.AGE)
                            .select(0, BlockModelGenerators.plainVariant(model0))
                            .select(1, BlockModelGenerators.plainVariant(model1))
                            .select(2, BlockModelGenerators.plainVariant(model2))));

            // C. SAPLINGS
            Identifier stemTexture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/sapling_stem");
            Identifier leafOverlayTexture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/sapling_leaves");
            TextureMapping saplingMap = new TextureMapping().put(STEM_SLOT, stemTexture).put(LEAVES_SLOT, leafOverlayTexture);
            Identifier saplingModel = TINTED_CROSS_OVERLAY.create(sapling, saplingMap, generator.modelOutput);
            generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(sapling, BlockModelGenerators.plainVariant(saplingModel)));
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

        itemModelGenerator.generateFlatItem(ModItems.ORGANIC_ASH, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.SOLAR_LENS, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BIOCOMPOSITE_PASTE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BIOPOLYMER, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BIOPOLYMER_CASING, ModelTemplates.FLAT_ITEM);



        itemModelGenerator.itemModelOutput.accept(
                ModBlocks.ROOT_BLOCK.asItem(),
                ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(ModBlocks.ROOT_BLOCK))
        );

        itemModelGenerator.itemModelOutput.accept(
                ModBlocks.MINERAL_SOIL.asItem(),
                ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(ModBlocks.MINERAL_SOIL))
        );

        Identifier stemTexture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/sapling_stem");
        Identifier saplingLeafTexture = Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "block/sapling_leaves");

        for (ResourceTree tree : TreeRegistry.TREES) {

            // 1. LEAVES
            Identifier leafBlockModel = ModelLocationUtils.getModelLocation(tree.getLeaves());
            itemModelGenerator.itemModelOutput.accept(
                    tree.getLeaves().asItem(),
                    ItemModelUtils.tintedModel(leafBlockModel, ItemModelUtils.constantTint(tree.getColor()))
            );

            // 2. FRUIT
            Identifier fruitBlockModel = ModelLocationUtils.getModelLocation(tree.getFruit(), "_stage2");
            itemModelGenerator.itemModelOutput.accept(
                    tree.getFruit().asItem(),
                    ItemModelUtils.plainModel(fruitBlockModel)
            );

            // 3. SAPLINGS
            Identifier saplingModelId = ModelTemplates.TWO_LAYERED_ITEM.create(
                    tree.getSapling().asItem(),
                    TextureMapping.layered(stemTexture, saplingLeafTexture),
                    itemModelGenerator.modelOutput
            );
            itemModelGenerator.itemModelOutput.accept(tree.getSapling().asItem(), ItemModelUtils.tintedModel(saplingModelId, ItemModelUtils.constantTint(0xFFFFFFFF), ItemModelUtils.constantTint(tree.getColor())));
        }
    }
}