package com.aureskull.zmcmod;

import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.renderer.ModBlockEntityRenderers;
import com.aureskull.zmcmod.block.ModBlockRenderLayerMaps;
import com.aureskull.zmcmod.client.MessageHudOverlay;
import com.aureskull.zmcmod.client.ModColorProviders;
import com.aureskull.zmcmod.event.ModKeyInputHandler;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.ModScreens;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
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
	}
}