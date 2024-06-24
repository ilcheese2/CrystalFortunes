package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.mixin.SpawnHelperInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

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
