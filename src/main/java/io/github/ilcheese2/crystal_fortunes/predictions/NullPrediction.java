package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public record NullPrediction() implements Prediction {

    public static final MapCodec<NullPrediction> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(NullPrediction::new));

    @Override
    public String toString() {
        return "Null";
    }

    @Override
    public void tick(World world) {
    }


    public static NullPrediction create(PlayerEntity playerEntity, CrystalBallBlockEntity blockEntity) {
        return null;
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.NULL;
    }
}
