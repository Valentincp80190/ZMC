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

		//TODO: BUG - Il arrive que des zombies soient manquants sur la map. Il faut que le mapController sache qu'il y a une différence entre le nombre de zombies à tuer et ceux présents sur la map pour refaire spawner un zombie au besoin.
		//TODO: BUG - Des zombies restent bloqués dans le porte de temps en temps
		//TODO: BUG - Quand la map démarre on passe à la manche 2 direct
		//TODO: BUG - Quand on quitte le monde et que l'on revient dessus, une nouvelle manche commence mais en plus avec une manche = manche + 1
		//TODO: BUG - Le joueur entend les sons de commencement de manche alors qu'il n'est pas sur la map
		//TODO: BUG - Voir pourquoi les zones controller continue de ticker même si l'on est à 1000 blocs

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