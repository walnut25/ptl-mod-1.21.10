package com.ptl;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

public class PtlModel extends EntityModel<LivingEntityRenderState> {

	private final ModelPart leftleg;
	private final ModelPart rightleg;
	private final ModelPart righthand;
	private final ModelPart lefthand;
	private final ModelPart body;
	private final ModelPart head;

	public PtlModel(ModelPart root) {
		super(root);
		this.leftleg = root.getChild("leftleg");
		this.rightleg = root.getChild("rightleg");
		this.righthand = root.getChild("righthand");
		this.lefthand = root.getChild("lefthand");
		this.body = root.getChild("body");
		this.head = root.getChild("head");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		// ⚠️ 我把你代码里的 pivot 全部换成了 of (适配 1.21.10 必须这么做)
		// 这里的坐标就是你刚才导出的，完全正确！

		modelPartData.addChild("leftleg", ModelPartBuilder.create().uv(7, 9).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
				ModelTransform.of(0.5F, 19.0F, -0.5F, 0.0F, 0.0F, 0.0F));

		modelPartData.addChild("rightleg", ModelPartBuilder.create().uv(1, 9).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
				ModelTransform.of(-0.5F, 19.0F, -0.5F, 0.0F, 0.0F, 0.0F));

		modelPartData.addChild("righthand", ModelPartBuilder.create().uv(9, 5).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
				ModelTransform.of(-1.5F, 17.0F, -0.5F, 0.0F, 0.0F, 0.0F));

		modelPartData.addChild("lefthand", ModelPartBuilder.create().uv(9, 1).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
				ModelTransform.of(1.5F, 17.0F, -0.5F, 0.0F, 0.0F, 0.0F));

		modelPartData.addChild("body", ModelPartBuilder.create().uv(1, 5).cuboid(-1.0F, -2.0F, -0.25F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, 19.0F, -0.75F, 0.0F, 0.0F, 0.0F));

		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -2.0F, -0.75F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
				ModelTransform.of(0.0F, 17.0F, -0.75F, 0.0F, 0.0F, 0.0F));

		// 如果你用回了 16x16 的小图，这里就留着 16, 16
		// 如果你还在用那张 512 的高清大图，记得改成 512, 512
		return TexturedModelData.of(modelData, 16, 16);
	}

	// =======================================================
	// 🏃‍♂️ 动画逻辑 (行走 + 手臂摆动)
	// =======================================================
	@Override
	public void setAngles(LivingEntityRenderState state) {
		super.setAngles(state);

		// 获取动画进度和幅度
		float time = state.limbSwingAnimationProgress;
		float speed = state.limbSwingAmplitude;

		// 1. 头部转动 (使用 relativeHeadYaw 自动计算差值)
		this.head.yaw = state.relativeHeadYaw * ((float)Math.PI / 180F);
		this.head.pitch = state.pitch * ((float)Math.PI / 180F);

		// 2. 腿部行走动画
		// 右腿：向前摆
		this.rightleg.pitch = (float)Math.cos(time * 0.6662F) * 1.4F * speed;
		// 左腿：向后摆 (相位差 PI)
		this.leftleg.pitch = (float)Math.cos(time * 0.6662F + (float)Math.PI) * 1.4F * speed;

		// 3. 手臂摆动动画 (自然走路姿势)
		// 右手：跟着左腿动 (相位差 PI)
		this.righthand.pitch = (float)Math.cos(time * 0.6662F + (float)Math.PI) * 1.4F * speed;
		// 左手：跟着右腿动
		this.lefthand.pitch = (float)Math.cos(time * 0.6662F) * 1.4F * speed;
	}
}