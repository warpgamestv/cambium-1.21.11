package com.warpgames.cambium.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ResourceTree {

    private final String name;
    private final int color; // Hex color (e.g. 0xFF0000)
    private final Item seedItem; // The item you put in the soil (e.g. Raw Iron)

    // We will store the blocks here after we register them
    private Block rootBlock;
    private Block soilBlock;
    private Block leafBlock;
    private Block fruitBlock;

    public ResourceTree(String name, int color, Item seedItem) {
        this.name = name;
        this.color = color;
        this.seedItem = seedItem;
    }

    public String getName() { return name; }
    public int getColor() { return color; }
    public Item getSeedItem() { return seedItem; }

    // Setters for registration (we'll use these in the next step)
    public void setBlocks(Block root, Block soil, Block leaf, Block fruit) {
        this.rootBlock = root;
        this.soilBlock = soil;
        this.leafBlock = leaf;
        this.fruitBlock = fruit;
    }

    public Block getRootBlock() { return rootBlock; }
    public Block getSoilBlock() { return soilBlock; }
    public Block getLeafBlock() { return leafBlock; }
    public Block getFruitBlock() { return fruitBlock; }
}