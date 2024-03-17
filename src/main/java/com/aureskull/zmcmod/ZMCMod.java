package com.aureskull.zmcmod;

import com.aureskull.zmcmod.block.ModBlocks;
import com.aureskull.zmcmod.block.entity.ModBlockEntities;
import com.aureskull.zmcmod.entity.ModEntities;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import com.aureskull.zmcmod.item.ModItemGroup;
import com.aureskull.zmcmod.item.ModItems;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.ModScreenHandlers;
import com.aureskull.zmcmod.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZMCMod implements ModInitializer {
	public static final String MOD_ID = "zmcmod";
	public static final Logger LOGGER = LoggerFactory.getLogger("zmcmod");

	@Override
	public void onInitialize() {
		//LOGGER.info("Hello Fabric world!");
		//TODO: Retirer le droit de drop des items lorsqu'une game est en cours (pour les joueurs qui jouent dans la map bien sûr).
		//TODO: Faire en sorte qu'un joueur ne perde pas son inventaire lorsqu'une game est en cours (pour les joueurs qui jouent dans la map toujours).
		//TODO: Faire un block joueur de son servant à mettre de l'ambiance. Dans ce block on pourrait configurer quel son à jouer et un random tick à mettre. On pourrait également le rendre activable via redstone

		ModItemGroup.registerItemGroups();
		ModItems.registerModItems();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		ModBlocks.registerModBlocks();
		ModMessages.registerC2SPackets();
		ModSounds.registerModSounds();

		ModEntities.registerModEntities();
		FabricDefaultAttributeRegistry.register(ModEntities.STANDING_ZOMBIE, StandingZombieEntity.createStandingZombieAttributes());
	}
}