package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.entities.SinEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class SinEntityModel<T extends LivingEntity> extends EntityModel<SinEntity<T>> {

    T entity;
    EntityType<T> type;
    EntityModel<T> model;

    public SinEntityModel(LivingEntityRenderer<T, EntityModel<T>> renderer, EntityType<T> type, T entity) {
        if (renderer == null) {
            model = null;
        }
        else {
            model = renderer.getModel();
        }
        this.type = type;
        this.entity = entity;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        model.render(matrices, vertices, light, overlay, color);
    }

    @Override
    public void animateModel(SinEntity entity2, float limbAngle, float limbDistance, float tickDelta) {
        if (entity == null) {
            return;
        }
        model.animateModel(entity, limbAngle, limbDistance, tickDelta);
    }

    @Override
    public void setAngles(SinEntity entity2, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        if (entity == null) {
            return;
        }
        model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
    }
}
