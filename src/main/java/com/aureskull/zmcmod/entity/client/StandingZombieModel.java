package com.aureskull.zmcmod.entity.client;

import com.aureskull.zmcmod.entity.animation.ModAnimations;
import com.aureskull.zmcmod.entity.custom.StandingZombieEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.math.MathHelper;

public class StandingZombieModel <T extends StandingZombieEntity> extends SinglePartEntityModel<T> {
	private final ModelPart body2;
	private final ModelPart head;

	public StandingZombieModel(ModelPart root) {
		this.body2 = root.getChild("body2");
		this.head = this.body2.getChild("top").getChild("head");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData body2 = modelPartData.addChild("body2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 1.0F));

		ModelPartData top = body2.addChild("top", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_arm = top.addChild("right_arm", ModelPartBuilder.create().uv(32, 0).cuboid(-3.0F, -2.0F, -4.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, -22.0F, 0.0F));

		ModelPartData left_arm = top.addChild("left_arm", ModelPartBuilder.create().uv(16, 32).cuboid(-1.0F, -2.0F, -4.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, -22.0F, 0.0F));

		ModelPartData body = top.addChild("body", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		ModelPartData head = top.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -3.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -23.0F, -3.0F, 0.3491F, 0.0F, 0.0F));

		ModelPartData bottom = body2.addChild("bottom", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_leg = bottom.addChild("left_leg", ModelPartBuilder.create().uv(0, 32).cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(1.9F, -12.0F, 0.0F));

		ModelPartData right_leg = bottom.addChild("right_leg", ModelPartBuilder.create().uv(24, 16).cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.9F, -12.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(StandingZombieEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);//Important
		this.setHeadAngles(netHeadYaw, headPitch);

		this.animateMovement(ModAnimations.WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.updateAnimation(entity.walkingAnimationSate, ModAnimations.WALK, ageInTicks, 1f);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
		headPitch = MathHelper.clamp(headPitch, -25.0f, 45.0f);

		this.head.yaw = headYaw * ((float)Math.PI / 180);
		this.head.pitch = headPitch * ((float)Math.PI / 180);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		body2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return body2;
	}
}