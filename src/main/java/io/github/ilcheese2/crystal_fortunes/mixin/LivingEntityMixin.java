package io.github.ilcheese2.crystal_fortunes.mixin;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.items.RoseGlassesItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onEquipStack", at = @At("HEAD"))
    void checkForGlasses(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
        if (((Entity) (Object) (this)).getWorld().isClient) {
            if (((Entity) (Object) this).getUuid() == MinecraftClient.getInstance().player.getUuid()) {
                if (slot == EquipmentSlot.HEAD && newStack.getItem() instanceof RoseGlassesItem) {
                    CrystalFortunesClient.requestShader(CrystalFortunesClient.LOVE_SHADER_2);
                } else {
                    CrystalFortunesClient.releaseShader(CrystalFortunesClient.LOVE_SHADER_2, true);
                }
            }
        }
    }
}
