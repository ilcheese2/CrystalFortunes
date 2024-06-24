package io.github.ilcheese2.crystal_fortunes.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface EntityRendererInvoker<T extends Entity> {
    @Invoker
    int invokeGetBlockLight(T entity, BlockPos pos);
}
