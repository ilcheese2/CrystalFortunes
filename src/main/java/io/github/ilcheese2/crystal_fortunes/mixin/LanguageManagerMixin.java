package io.github.ilcheese2.crystal_fortunes.mixin;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    @Inject(method = "reload", at = @At("TAIL"))
    void rebuildTranslations(ResourceManager manager, CallbackInfo ci) {
        CrystalFortunesClient.buildTranslations();
    }
}
