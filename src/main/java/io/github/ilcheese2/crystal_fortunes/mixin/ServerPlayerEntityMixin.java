package io.github.ilcheese2.crystal_fortunes.mixin;

import io.github.ilcheese2.crystal_fortunes.camera.CameraData;
import io.github.ilcheese2.crystal_fortunes.camera.ServerCameraHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setCameraEntity(Lnet/minecraft/entity/Entity;)V", ordinal = 0))
    void fixPlayerPositionAndGameMode(CallbackInfo ci) {
        CameraData.fixPlayer((ServerPlayerEntity) (Object) this);
    }
}
