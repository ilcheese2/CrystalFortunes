package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record NullPrediction() implements Prediction {

    public static final MapCodec<NullPrediction> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(NullPrediction::new));

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public void tick(World world) {
    }

    @Override
    public void cleanup(World world) {
    }

    public static NullPrediction create(PlayerEntity playerEntity, BlockPos pos) {
        return null;
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.NULL;
    }
}
