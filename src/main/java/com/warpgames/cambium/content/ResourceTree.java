package com.warpgames.cambium.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ResourceTree {
    private final String name;
    private final int color;
    private final Item item; // The item this tree produces (e.g. Raw Iron)

    private Block log;
    private Block leaves;
    private Block fruit;
    private Block sapling;

    public ResourceTree(String name, int color, Item item) {
        this.name = name;
        this.color = color;
        this.item = item;
    }

    public String getName() { return name; }
    public int getColor() { return color; }
    public Item getItem() { return item; } // Fixes ResourceFruitBlock error

    // --- SETTERS (Fixes ModBlocks error) ---
    public void setLog(Block log) { this.log = log; }
    public Block getLog() { return log; }

    public void setLeaves(Block leaves) { this.leaves = leaves; }
    public Block getLeaves() { return leaves; }

    public void setFruit(Block fruit) { this.fruit = fruit; }
    public Block getFruit() { return fruit; }

    public void setSapling(Block sapling) {this.sapling = sapling; }
    public Block getSapling() {return sapling; }
}