package io.github.ilcheese2.crystal_fortunes.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnHelper.class)
public interface SpawnHelperInvoker {
    @Invoker
    static BlockPos invokeGetEntitySpawnPos(WorldView world, EntityType<?> entityType, int x, int z) {
        throw new AssertionError();
    }
}
