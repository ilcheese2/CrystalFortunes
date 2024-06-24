package io.github.ilcheese2.crystal_fortunes.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.ilcheese2.crystal_fortunes.blocks.CrystalBallBlock;
import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.items.RingItem;
import io.github.ilcheese2.crystal_fortunes.predictions.LovePrediction;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapWithCondition(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    boolean removeTilt(GameRenderer instance, MatrixStack matrices, float tickDelta) {
        return  !(CrystalFortunesClient.needLoveEffects());
    }
    @WrapWithCondition(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    boolean removeTilt2(GameRenderer instance, MatrixStack matrices, float tickDelta) {
        return  !(CrystalFortunesClient.needLoveEffects());
    }
    @Inject(method = "onCameraEntitySet", at = @At("HEAD"))
    void setRedShader(Entity entity, CallbackInfo ci) {
        if (entity instanceof RabbitEntity rabbit) {
            if (rabbit.getVariant() == RabbitEntity.RabbitType.EVIL) {
                CrystalFortunesClient.requestShader(CrystalFortunesClient.RED_SHADER);
            }
        }
        else {
            CrystalFortunesClient.releaseShader(CrystalFortunesClient.RED_SHADER);
        }
    }
}
