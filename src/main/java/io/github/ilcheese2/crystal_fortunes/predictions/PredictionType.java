package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PredictionType<T extends Prediction>(MapCodec<T> codec, PredictionFactory<T> factory) {
    public static final Registry<PredictionType<?>> PREDICTION_REGISTRY = new SimpleRegistry<>(
        RegistryKey.ofRegistry(Identifier.of(CrystalFortunes.MODID, "predictions")), Lifecycle.stable());

    public static final PredictionType<WindfallPrediction> WINDFALL = register("windfall", new PredictionType<>(WindfallPrediction.CODEC, WindfallPrediction::new));
    public static final PredictionType<EvilBeastPrediction> EVIL_BEAST = register("beast", new PredictionType<>(EvilBeastPrediction.CODEC, EvilBeastPrediction::create)); // records fucking suck
    public static final PredictionType<LovePrediction> LOVE = register("love", new PredictionType<>(LovePrediction.CODEC, LovePrediction::create));
    public static final PredictionType<NeverHungryPrediction> NEVER_HUNGRY = register("never_hungry", new PredictionType<>(NeverHungryPrediction.CODEC, NeverHungryPrediction::new));
    public static final PredictionType<SinPrediction> SIN = register("sin", new PredictionType<>(SinPrediction.CODEC, SinPrediction::create));
    public static final PredictionType<NullPrediction> NULL = register("null", new PredictionType<>(NullPrediction.CODEC, NullPrediction::create));

    public static final Codec<Prediction> CODEC = PREDICTION_REGISTRY.getCodec().dispatch("type", Prediction::getType, PredictionType::codec);

    public static <T extends Prediction> PredictionType<T> register(String id, PredictionType<T> predictionType) {
        return Registry.register(PredictionType.PREDICTION_REGISTRY, Identifier.of(CrystalFortunes.MODID, id), predictionType);
    }

    @FunctionalInterface
    public interface PredictionFactory<T extends Prediction> {
        T create(PlayerEntity player, BlockPos pos);
    }
}
