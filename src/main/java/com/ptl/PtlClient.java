package com.ptl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents; // 导入生命周期事件
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PtlClient implements ClientModInitializer {

    public static final EntityModelLayer PTL_LAYER = new EntityModelLayer(Identifier.of(Ptl.MOD_ID, "ptl_model"), "main");

    @Override
    public void onInitializeClient() {
        // 1. 注册渲染器
        EntityRendererRegistry.register(Ptl.PTL_ENTITY, PtlRenderer::new);

        // 2. 注册模型数据
        EntityModelLayerRegistry.registerModelLayer(PTL_LAYER, PtlModel::getTexturedModelData);

        // ❌ 以前的上色代码全删掉！我们要用图片了！
    }

    // ==========================================
    // 生物渲染器
    // ==========================================
    private static class PtlRenderer extends MobEntityRenderer<PtlEntity, LivingEntityRenderState, PtlModel> {

        public PtlRenderer(EntityRendererFactory.Context context) {
            super(context, new PtlModel(context.getPart(PTL_LAYER)), 0.5f);
        }

        @Override
        public LivingEntityRenderState createRenderState() {
            return new LivingEntityRenderState();
        }

        @Override
        public Identifier getTexture(LivingEntityRenderState state) {
            return Identifier.of(Ptl.MOD_ID, "textures/entity/peng_tie_lin.png");
        }

        @Override
        protected void scale(LivingEntityRenderState state, MatrixStack matrices) {
            // 1. 先做向下平移修正 (解决脚不沾地的问题)
            matrices.translate(0.0F, 10.0F / 16.0F, 0.0F);

            // 2. 设定基础放大倍数 (成年体是 3.5 倍)
            float size = 3.5F;

            // 3. 【关键】如果是宝宝，就把体型缩小一半
            // state.baby 是 1.21+ 新版本自带的状态判断
            if (state.baby) {
                size *= 0.5F; // 3.5 * 0.5 = 1.75 倍

                // 可选：为了让宝宝看起来更萌，Minecraft 原版通常会把宝宝的头放大
                // 但因为我们是整体缩放，这里只是单纯变小只
                matrices.translate(0.0F, -0.25F, 0.0F);
            }


            // 4. 应用缩放
            matrices.scale(size, size, size);
        }
    }
}