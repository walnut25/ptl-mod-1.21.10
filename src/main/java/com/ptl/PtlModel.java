package com.ptl;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState; // 关键引用

// 【修改点 1】泛型改为 LivingEntityRenderState
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

		// ==========================================
		// 👇 这里是你原本的模型数据，完全没动 👇
		// ==========================================
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

		// 保持原样 (如果你用的是 512 的贴图，记得这里手动改一下，如果还是用小图就不用动)
		return TexturedModelData.of(modelData, 16, 16);
	}

	// =======================================================
	// 🏃‍♂️ 动画逻辑 (已修复参数类型)
	// =======================================================
	// 【修改点 2】参数改为 LivingEntityRenderState
	@Override
	public void setAngles(LivingEntityRenderState state) {
		super.setAngles(state);

		float time = state.limbSwingAnimationProgress;
		float speed = state.limbSwingAmplitude;

		// 头部转动 (使用 relativeHeadYaw)
		// 【重要】之前这里只计算没赋值，现在加上了赋值代码
		this.head.yaw = state.relativeHeadYaw * ((float)Math.PI / 180F);
		this.head.pitch = state.pitch * ((float)Math.PI / 180F);

		// 腿部行走动画
		this.rightleg.pitch = (float)Math.cos(time * 0.6662F) * 1.4F * speed;
		this.leftleg.pitch = (float)Math.cos(time * 0.6662F + (float)Math.PI) * 1.4F * speed;

		// 手臂摆动动画
		this.righthand.pitch = (float)Math.cos(time * 0.6662F + (float)Math.PI) * 1.4F * speed;
		this.lefthand.pitch = (float)Math.cos(time * 0.6662F) * 1.4F * speed;
	}
}