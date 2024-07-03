package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.entities.SinEntity;
import io.github.ilcheese2.crystal_fortunes.mixin.EntityRendererInvoker;
import io.github.ilcheese2.crystal_fortunes.mixin.LivingEntityRendererInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@SuppressWarnings({"rawtypes", "unchecked"}) // fuck generics
public class SinEntityRenderer extends MobEntityRenderer {
    record EntityRenderData <T extends LivingEntity> (SinEntityModel<T> model, LivingEntityRenderer<T, ?> renderer, T entity) {}
    EntityRendererFactory.Context ctx;
    static final HashMap<EntityType, EntityRenderData> renderDatas = new HashMap<>();


    public SinEntityRenderer(EntityRendererFactory.Context ctx, SinEntityModel sinEntityModel, float v) {
        super(ctx, sinEntityModel, 0.0f);
        this.ctx = ctx;
    }


    public static SinEntityRenderer createRenderer (EntityRendererFactory.Context ctx) {
        return new SinEntityRenderer(ctx, new SinEntityModel(null, null, null), 0.4f);
    }

    @Override
    public Identifier getTexture(Entity entity) {
        if (((SinEntity) entity).appearanceType == null) {
            return Identifier.ofVanilla("textures/entity/cow/cow.png");
        }
        return getRenderData(((SinEntity)entity).appearanceType).renderer().getTexture(getRenderData(((SinEntity)entity).appearanceType).entity);
    }


    @Override
    protected int getBlockLight(Entity entity, BlockPos pos) {
        if (((SinEntity) entity).appearanceType == null) {
            return 0;
        }
        return ((EntityRendererInvoker) getRenderData(((SinEntity) entity).appearanceType).renderer).invokeGetBlockLight(getRenderData(((SinEntity) entity).appearanceType).entity, pos);
    }

    private <T extends LivingEntity> EntityRenderData<T> getRenderData(EntityType<T> type) {
        return renderDatas.computeIfAbsent(type, (t) -> {
            T entity = (T) t.create(MinecraftClient.getInstance().world);
            var renderer1 = ctx.getRenderDispatcher().getRenderer(entity);
            return new EntityRenderData<>(new SinEntityModel<T>((LivingEntityRenderer<T, EntityModel<T>>) renderer1, t, entity), (LivingEntityRenderer<T, EntityModel<T>>) renderer1, entity);
        });
    }

    @Override
    public void render(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (((SinEntity) livingEntity).appearanceType == null) {
            return;
        }
        this.model = getRenderData(((SinEntity) livingEntity).appearanceType).model;
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        RenderLayer baseLayer = super.getRenderLayer(entity, showBody, true, showOutline);
        return baseLayer == null ? null : CrystalFortunesClient.GHOST_SHADER.getRenderLayer(baseLayer);
    }

    @Override
    protected float getAnimationProgress(LivingEntity entity, float tickDelta) {
        if (((SinEntity) entity).appearanceType == null) {
            return 0.0f;
        }
        return ((LivingEntityRendererInvoker) getRenderData(((SinEntity) entity).appearanceType).renderer).invokeGetAnimationProgress(getRenderData(((SinEntity) entity).appearanceType).entity, tickDelta);
    }
}
