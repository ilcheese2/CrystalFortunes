package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class HolyGrenadeEntityRenderer extends EntityRenderer {

    EntityRendererFactory.Context ctx;

    public HolyGrenadeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    @Override
    public void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        ctx.getBlockRenderManager().renderBlock(CrystalFortunes.HOLY_GRENADE_BLOCK.getDefaultState(), entity.getBlockPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, entity.getRandom());
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return null;
    }
}
