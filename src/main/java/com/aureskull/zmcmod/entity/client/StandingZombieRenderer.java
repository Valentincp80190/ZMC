package com.aureskull.zmcmod.entity.client;

import com.aureskull.zmcmod.ZMCMod;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StandingZombieRenderer extends MobEntityRenderer<StandingZombieEntity, StandingZombieModel<StandingZombieEntity>> {
    private static final Identifier Texture = new Identifier(ZMCMod.MOD_ID, "textures/entity/standing_zombie.png");

    public StandingZombieRenderer(EntityRendererFactory.Context context) {
        super(context, new StandingZombieModel<>(context.getPart(ModModelLayers.STANDING_ZOMBIE)), 0.6f);
    }

    @Override
    public Identifier getTexture(StandingZombieEntity entity) {
        return Texture;
    }

    @Override
    public void render(StandingZombieEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
