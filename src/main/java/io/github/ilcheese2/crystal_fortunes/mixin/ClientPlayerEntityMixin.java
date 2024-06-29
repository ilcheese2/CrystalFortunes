package io.github.ilcheese2.crystal_fortunes.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    boolean removeSlowdownWithRing(boolean original) {
        if (((LivingEntity) (Object) this).getActiveItem().isOf(CrystalFortunes.RING)) {
            return false;
        }
        return original;
    }
}
