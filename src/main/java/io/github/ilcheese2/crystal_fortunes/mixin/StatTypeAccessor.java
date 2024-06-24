package io.github.ilcheese2.crystal_fortunes.mixin;

import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StatType.class)
public interface StatTypeAccessor {
    @Accessor
    <T> Map<T, Stat<T>> getStats();
}
