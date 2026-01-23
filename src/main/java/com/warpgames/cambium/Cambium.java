package com.warpgames.cambium;

import com.warpgames.cambium.recipe.ModRecipes;
import com.warpgames.cambium.registry.*;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cambium implements ModInitializer {
	public static final String MOD_ID = "cambium";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		ModRecipes.registerRecipes();
		ModItemGroups.registerItemGroups();

		LOGGER.info("Hello Fabric world!");
	}
}