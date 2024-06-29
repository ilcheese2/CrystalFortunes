package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.mixin.BlockEntityAccessor;
import io.github.ilcheese2.crystal_fortunes.predictions.NullPrediction;
import io.github.ilcheese2.crystal_fortunes.predictions.Prediction;
import io.github.ilcheese2.crystal_fortunes.predictions.WheelPrediction;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hyper_pigeon.wacky_wheel.block.entity.WackyWheelBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class WheelRenderer implements HudRenderCallback {

    public static WackyWheelBlockEntity wackyWheel = null;

    public WheelRenderer() {

    }

    public static void handlePrediction(Prediction prediction) {
        if (prediction instanceof WheelPrediction wheel) {
            WheelRenderer.wackyWheel = wheel.wheel();
            wackyWheel.setWorld(MinecraftClient.getInstance().world);
        }
        if (prediction instanceof NullPrediction) {
            WheelRenderer.wackyWheel = null;
        }
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        if (wackyWheel == null || !(CrystalFortunesClient.prediction instanceof WheelPrediction)) {
            return;
        }
        World world = MinecraftClient.getInstance().world;
        float scale = 55;
        wackyWheel.setWorld(world);
        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth()/2d + 27d, context.getScaledWindowHeight()/2d + 25d, 0);
        context.getMatrices().scale(scale, scale, -scale);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        ((BlockEntityAccessor) wackyWheel).setPos(MinecraftClient.getInstance().player.getBlockPos());
        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(wackyWheel).render(wackyWheel, tickCounter.getTickDelta(false), context.getMatrices(), context.getVertexConsumers(), 0, OverlayTexture.DEFAULT_UV);
        context.draw();
        context.getMatrices().pop();
    }
}
