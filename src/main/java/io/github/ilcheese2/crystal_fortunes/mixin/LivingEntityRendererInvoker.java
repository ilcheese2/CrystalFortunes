package io.github.ilcheese2.crystal_fortunes.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererInvoker {

    @Invoker
    float invokeGetAnimationProgress(LivingEntity entity, float tickDelta);
}
