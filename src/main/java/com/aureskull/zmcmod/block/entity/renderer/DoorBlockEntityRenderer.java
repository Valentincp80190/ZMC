package com.aureskull.zmcmod.block.entity.renderer;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.block.entity.SmallZombieWindowBlockEntity;
import com.aureskull.zmcmod.block.entity.ZombieSpawnerBlockEntity;
import com.aureskull.zmcmod.block.entity.ZoneControllerBlockEntity;
import com.aureskull.zmcmod.block.entity.door.DoorBlockEntity;
import com.aureskull.zmcmod.item.custom.Linker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class DoorBlockEntityRenderer implements BlockEntityRenderer<DoorBlockEntity> {
    private final Identifier LINK_TEST = new Identifier(ZMCMod.MOD_ID, "textures/gui/sprites/link_test.png");

    public DoorBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    @Override
    public void render(DoorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(entity.getMasterPos() != null) return;


        try{
            List<BlockPos> zoneControllers = entity.getLink(ZoneControllerBlockEntity.class);

            if(zoneControllers != null){
                MinecraftClient minecraftClient = MinecraftClient.getInstance();

                // Check if the Minecraft client and the player are not null
                if (minecraftClient != null && minecraftClient.player != null) {
                    ItemStack itemInMainHand = minecraftClient.player.getMainHandStack();

                    // Check if the item in the main hand is the Linker item
                    if (itemInMainHand.getItem() instanceof Linker) {
                        for(BlockPos pos : zoneControllers)
                            renderLine(entity.getPos(), pos, matrices);
                    }
                }
            }
        }catch (Exception e){

        }
    }

    private void renderLine(BlockPos posA, BlockPos posB, MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        //RenderSystem.enableDepthTest();

        double deltaX = posB.getX() - posA.getX();
        double deltaY = posB.getY() - posA.getY();
        double deltaZ = posB.getZ() - posA.getZ();

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        buffer.vertex(positionMatrix, 0.5F, 1F, 0.5F).color(0f, 1f, 0f, 1f).next();
        buffer.vertex(positionMatrix, (float) deltaX + 0.5F, (float) deltaY + 0.5F, (float) deltaZ + 0.5F).color(1f, 0f, 0f, 1f).next();

        Tessellator.getInstance().draw();
    }
}
