package com.aureskull.zmcmod;

import com.aureskull.zmcmod.block.entity.renderer.ModBlockEntityRenderers;
import com.aureskull.zmcmod.block.ModBlockRenderLayerMaps;
import com.aureskull.zmcmod.client.MessageHudOverlay;
import com.aureskull.zmcmod.client.ModColorProviders;
import com.aureskull.zmcmod.entity.ModEntities;
import com.aureskull.zmcmod.entity.client.ModModelLayers;
import com.aureskull.zmcmod.entity.client.StandingZombieModel;
import com.aureskull.zmcmod.entity.client.StandingZombieRenderer;
import com.aureskull.zmcmod.event.ModKeyInputHandler;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZMCModClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("zmcmod");

	@Override
	public void onInitializeClient() {
		ModMessages.registerS2CPackets();
		ModKeyInputHandler.register();
		ModScreens.registerScreens();
		ModBlockEntityRenderers.registerBlockEntityRenderers();
		ModBlockRenderLayerMaps.putBlocks();
		ModColorProviders.registerColorProviders();
		HudRenderCallback.EVENT.register(new MessageHudOverlay());

		EntityRendererRegistry.register(ModEntities.STANDING_ZOMBIE, StandingZombieRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.STANDING_ZOMBIE, StandingZombieModel::getTexturedModelData);

		//ctrl e + h
	}
}