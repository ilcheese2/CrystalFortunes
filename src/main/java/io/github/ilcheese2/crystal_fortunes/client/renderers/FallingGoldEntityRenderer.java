package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.entities.FallingGoldEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.RotationAxis;

public class FallingGoldEntityRenderer extends FallingBlockEntityRenderer {
    public FallingGoldEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FallingBlockEntity fallingBlockEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale( FallingGoldEntity.SIZE, FallingGoldEntity.SIZE, FallingGoldEntity.SIZE);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(fallingBlockEntity.getYaw()));
        super.render(fallingBlockEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}
