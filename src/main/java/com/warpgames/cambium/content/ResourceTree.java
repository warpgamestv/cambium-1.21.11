package com.warpgames.cambium.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

public class ResourceTree {
    private final String name;
    private final String modId;
    private final int color;
    private final Supplier<Item> itemSupplier;
    private final String rawItemId;

    private Block log;
    private Block leaves;
    private Block fruit;
    private Block sapling;

    public String getName() { return name; }
    public String getModId() { return modId; }
    public int getColor() { return color; }
    public Item getItem() { return itemSupplier.get(); }
    public String getRawItemId() { return rawItemId; }

    public ResourceTree(String name, String modId, int color, Supplier<Item> itemSupplier) {
        this(name, modId, color, itemSupplier, "");
    }

    // Constructor 2: String ID (Modded)
    public ResourceTree(String name, String modId, int color, Supplier<Item> itemSupplier, String rawItemId) {
        this.name = name;
        this.modId = modId;
        this.color = color;
        this.itemSupplier = itemSupplier;
        this.rawItemId = rawItemId;
    }


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