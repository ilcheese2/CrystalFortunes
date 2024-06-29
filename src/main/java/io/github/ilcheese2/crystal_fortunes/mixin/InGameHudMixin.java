package io.github.ilcheese2.crystal_fortunes.mixin;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @ModifyArgs(method="renderHealthBar", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIZZZ)V"))
    void renderNormalHearts(Args args) {
        if (CrystalFortunesClient.needLoveEffects()) {
            if (args.get(1) != InGameHud.HeartType.ABSORBING && args.get(1) != InGameHud.HeartType.CONTAINER) {
                args.set(1, InGameHud.HeartType.NORMAL);
            }
            ;
            args.set(5, false);
            args.set(6, false);
        }
    }
    @ModifyArgs(method ="renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
    void renderFullHealthBar(Args args) {
        if (CrystalFortunesClient.needLoveEffects()) {
            args.set(7, (int) (float) args.get(6));
            args.set(8, (int) (float) args.get(6));
        }
    }
}
