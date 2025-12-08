package com.ptl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState; // 基础状态类
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PtlClient implements ClientModInitializer {

    public static final EntityModelLayer PTL_LAYER = new EntityModelLayer(Identifier.of(Ptl.MOD_ID, "ptl_model"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Ptl.PTL_ENTITY, PtlRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PTL_LAYER, PtlModel::getTexturedModelData);
        // ... (如果你有注册颜色代码，也放在这，没有就算了) ...
    }

    // ==========================================
    // 1. 定义自定义状态类 (继承自 LivingEntityRenderState)
    // 专门用来存 "是否受伤" 这个额外信息
    // ==========================================
    public static class PtlRenderState extends LivingEntityRenderState {
        public boolean isHurt; // 是不是在挨打
    }

    // ==========================================
    // 2. 修改渲染器泛型
    // 把原来的 LivingEntityRenderState 改成我们自己的 PtlRenderState
    // ==========================================
    private static class PtlRenderer extends MobEntityRenderer<PtlEntity, PtlRenderState, PtlModel> {

        // 定义两张皮肤的 ID
        private static final Identifier TEXTURE_NORMAL = Identifier.of(Ptl.MOD_ID, "textures/entity/peng_tie_lin.png");
        private static final Identifier TEXTURE_HURT = Identifier.of(Ptl.MOD_ID, "textures/entity/peng_tie_lin_hurt.png");

        public PtlRenderer(EntityRendererFactory.Context context) {
            super(context, new PtlModel(context.getPart(PTL_LAYER)), 0.5f);
        }

        // 创建状态时，new 我们自己的类
        @Override
        public PtlRenderState createRenderState() {
            return new PtlRenderState();
        }

        // 【关键】更新状态：从实体读取数据，填入 state
        @Override
        public void updateRenderState(PtlEntity entity, PtlRenderState state, float tickDelta) {
            super.updateRenderState(entity, state, tickDelta);

            // 如果受伤时间 > 0，说明正在挨打
            // 或者如果死亡了 (deathTime > 0)，也可以显示痛苦面具
            state.isHurt = entity.hurtTime > 0 || entity.deathTime > 0;
        }

        // 【关键】获取贴图：根据 state 里的数据决定用哪张图
        @Override
        public Identifier getTexture(PtlRenderState state) {
            if (state.isHurt) {
                return TEXTURE_HURT; // 痛！
            }
            return TEXTURE_NORMAL; // 正常
        }

        @Override
        protected void scale(PtlRenderState state, MatrixStack matrices) {
            matrices.translate(0.0F, 10.0F / 16.0F, 0.0F);
            float size = 3.5F;
            if (state.baby) {
                size *= 0.5F;
                matrices.translate(0.0F, -0.25F, 0.0F);
            }
            matrices.scale(size, size, size);
        }
    }
}