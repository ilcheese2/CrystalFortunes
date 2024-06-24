package io.github.ilcheese2.crystal_fortunes.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow private ClientWorld world;
    @Unique
    int timer = -1;

    @ModifyExpressionValue(method = "onWorldTimeUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/WorldTimeUpdateS2CPacket;getTimeOfDay()J"))
    long keepChangedTimeOfDay(long timeOfDay) {
        if (Math.abs(timeOfDay - this.world.getTimeOfDay()) > 10) {
            if (timer == -1) {
                timer = 6;
            } else if(timer == 0) {
                timer = -1;
                return timeOfDay;
            }
            return this.world.getTimeOfDay();
        } else {
            timer = -1;
            return timeOfDay;
        }
    }

    @Inject(method ="tick", at =@At("HEAD"))
    private void tick(CallbackInfo info) {
        if (timer != 0 && timer != -1) {
            timer--;
        }
    }
}
