package io.github.ilcheese2.crystal_fortunes.predictions;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
public class PolymerCompat {
    public static boolean checkPolymer(EntityType<?> type, World world) {
        return type.create(world) instanceof PolymerEntity;
    }
}
