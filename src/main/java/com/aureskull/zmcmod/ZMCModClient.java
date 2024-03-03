package com.aureskull.zmcmod;

import com.aureskull.zmcmod.event.ModKeyInputHandler;
import com.aureskull.zmcmod.networking.ModMessages;
import com.aureskull.zmcmod.screen.MapControllerScreen;
import com.aureskull.zmcmod.screen.ModScreenHandlers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
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

		HandledScreens.register(ModScreenHandlers.MAP_CONTROLLER_SCREEN_HANDLER, MapControllerScreen::new);

		drawCube(new BlockPos(57,33,58), new BlockPos(51,37,46));
	}

	//Créer une classe ShapeRendererManager
	private void drawCube(BlockPos posA, BlockPos posB) {
		int x1 = posA.getX();
		int x2 = posB.getX();
		
		int y1 = posA.getY();
		int y2 = posB.getY();
		
		int z1 = posA.getZ();
		int z2 = posB.getZ();

		if(x1 < x2)
			x2+=1;
		else if(x1 > x2)
			x1+=1;

		if(y1 < y2)
			y2+=1;
		else if(y1 > y2)
			y1+=1;

		if(z1 < z2)
			z2+=1;
		else if(z1 > z2)
			z1+=1;

		final int f_x1 = x1;
		final int f_x2 = x2;
		final int f_y1 = y1;
		final int f_y2 = y2;
		final int f_z1 = z1;
		final int f_z2 = z2;

		WorldRenderEvents.END.register(context -> {
			Camera camera = context.camera();

			Vec3d targetPosition = new Vec3d(0, 0, 0);
			Vec3d transformedPosition = targetPosition.subtract(camera.getPos());

			MatrixStack matrixStack = new MatrixStack();
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
			matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);

			Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(1f, 0f, 0f, 1f).next();

			//Arrêtes verticales
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(1f, 0f, 0f, 1f).next();

			buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(1f, 0f, 0f, 1f).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(1f, 0f, 0f, 1f).next();

			RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
			//Sans l'image, on ne peut pas afficher les faces du cube => A corriger
			RenderSystem.setShaderTexture(0, new Identifier("zmcmod", "test.png"));
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			RenderSystem.disableCull();

			tessellator.draw();

			RenderSystem.enableCull();
		});

		//Représentation des faces du cube
		WorldRenderEvents.END.register(context -> {
			Camera camera = context.camera();

			Vec3d targetPosition = new Vec3d(0, 0, 0);
			Vec3d transformedPosition = targetPosition.subtract(camera.getPos());

			MatrixStack matrixStack = new MatrixStack();
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
			matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);

			Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			// Définir la fonction de mélange pour rendre le carré semi-transparent
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();

			buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

			float alpha = .11f;

			buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(1f, 0f, 0f, alpha).next();

			buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(1f, 0f, 0f, alpha).next();

			buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(1f, 0f, 0f, alpha).next();

			buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(1f, 0f, 0f, alpha).next();

			buffer.vertex(positionMatrix, f_x1, f_y1, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y1, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y1, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y1, f_z2).color(1f, 0f, 0f, alpha).next();

			buffer.vertex(positionMatrix, f_x1, f_y2, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z1).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x2, f_y2, f_z2).color(1f, 0f, 0f, alpha).next();
			buffer.vertex(positionMatrix, f_x1, f_y2, f_z2).color(1f, 0f, 0f, alpha).next();

			//Va permettre de rendre les faces du cubes visible peu importe l'angle de vu
			RenderSystem.disableCull();

			tessellator.draw();

			// Réinitialiser la fonction de mélange à son état par défaut
			RenderSystem.disableBlend();
			RenderSystem.enableCull();
		});
	}
}