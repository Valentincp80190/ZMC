package com.aureskull.zmcmod;

import com.aureskull.zmcmod.block.ModBlocks;
import com.aureskull.zmcmod.block.entity.ModBlockEntities;
import com.aureskull.zmcmod.item.ModItemGroup;
import com.aureskull.zmcmod.item.ModItems;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZMCMod implements ModInitializer {
	public static final String MOD_ID = "zmcmod";
	public static final Logger LOGGER = LoggerFactory.getLogger("zmcmod");

	@Override
	public void onInitialize() {
		//LOGGER.info("Hello Fabric world!");
		ModItemGroup.registerItemGroups();
		ModItems.registerModItems();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		ModBlocks.registerModBlocks();
		ModMessages.registerC2SPackets();
	}
}