package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.entities.SinEntity;
import io.github.ilcheese2.crystal_fortunes.mixin.EntityRendererInvoker;
import io.github.ilcheese2.crystal_fortunes.mixin.EntityRenderersAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SinEntityRenderer extends MobEntityRenderer {
    record EntityRenderData <T extends LivingEntity> (SinEntityModel<T> model, LivingEntityRenderer<T, ?> renderer, T entity) {}
    EntityRendererFactory.Context ctx;
    static final HashMap<EntityType, EntityRenderData> renderDatas = new HashMap<>();


    public SinEntityRenderer(EntityRendererFactory.Context ctx, SinEntityModel sinEntityModel, float v) {
        super(ctx, sinEntityModel, 0.0f);
        this.ctx = ctx;
    }


    public static SinEntityRenderer createRenderer (EntityRendererFactory.Context ctx) {
        LivingEntityRenderer<?, ?> renderer1 = (LivingEntityRenderer<?, ?>) EntityRenderersAccessor.getRENDERER_FACTORIES().get(EntityType.ARMADILLO).create(ctx); // fuck it we ball
        SinEntityRenderer renderer2 = new SinEntityRenderer(ctx, new SinEntityModel(renderer1, EntityType.ARMADILLO, null), 0.4f);
        return renderer2;
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return getRenderData(((SinEntity)entity).appearanceType).renderer().getTexture(getRenderData(((SinEntity)entity).appearanceType).entity);
    }


    @Override
    protected int getBlockLight(Entity entity, BlockPos pos) {
        return ((EntityRendererInvoker) getRenderData(((SinEntity) entity).appearanceType).renderer).invokeGetBlockLight(getRenderData(((SinEntity) entity).appearanceType).entity, pos);
    }

    private <T extends LivingEntity> EntityRenderData<T> getRenderData(EntityType<T> type) {
        return renderDatas.computeIfAbsent(type, (t) -> {
            LivingEntityRenderer<T, ?> renderer1 = (LivingEntityRenderer<T, ?>) EntityRenderersAccessor.getRENDERER_FACTORIES().get(type).create(ctx);
            T entity = (T) t.create(MinecraftClient.getInstance().world);
            return new EntityRenderData<>(new SinEntityModel<T>(renderer1, t, entity), renderer1, entity);
        });
    }

    @Override
    public void render(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.model = getRenderData(((SinEntity) livingEntity).appearanceType).model;
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        RenderLayer baseLayer = super.getRenderLayer(entity, showBody, true, showOutline);
        return baseLayer == null ? null : CrystalFortunesClient.GHOST_SHADER.getRenderLayer(baseLayer);
    }
}
