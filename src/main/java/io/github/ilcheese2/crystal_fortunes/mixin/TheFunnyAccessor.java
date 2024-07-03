package io.github.ilcheese2.crystal_fortunes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import symbolics.division.honque.magic.Honk;

@Mixin(targets = "symbolics/division/honque/TheFunny")
public interface TheFunnyAccessor {
    @Accessor()
    Honk getWhatIDo();
}
