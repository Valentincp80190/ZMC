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
		//TODO: Retirer le droit de drop des items lorsqu'une game est en cours (pour les joueurs qui jouent dans la map bien sûr).
		//TODO: Faire en sorte qu'un joueur ne perde pas son inventaire lorsqu'une game est en cours (pour les joueurs qui jouent dans la map toujours).
		//TODO: Faire un block joueur de son servant à mettre de l'ambiance. Dans ce block on pourrait configurer quel son à jouer et un random tick à mettre. On pourrait également le rendre activable via redstone

		//TODO: BUG - Lorsqu'un joueur répare une porte au moment où un zombie arrive dessus, le zombie se déplace légérement de la porte et celui-ci ne peut plus passer au travers
		//TODO: BUG - Les zombies n'arrivent plus à retirer les planches sur certaines porte. Ce bug arrive quand plusieurs porte sont côte à côte. Si j'en retire une, le problème se résou car ils veulent en fait retirer les planches de la porte à côté
		//TODO: BUG - Il arrive que des zombies soient manquants sur la map. Il faut que le mapController sache qu'il y a une différence entre le nombre de zombies à tuer et ceux présents sur la map pour refaire spawner un zombie au besoin.
		//TODO: BUG - Les zombies ne regardent pas leur portes
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