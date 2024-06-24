package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.entities.FallingGoldEntity;
import io.github.ilcheese2.crystal_fortunes.entities.SinEntity;
import io.github.ilcheese2.crystal_fortunes.mixin.StatTypeAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

public record SinPrediction(UUID player, UUID sin) implements Prediction {


    private final static double Y_DISTANCE_MIN = 1;
    private final static double Y_DISTANCE_MAX = 3;
    private final static double XZ_DISTANCE_MIN = 4;
    private final static double XZ_DISTANCE_MAX = 8;
    private static int timer = -1;

    public static final MapCodec<SinPrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Uuids.CODEC.fieldOf("player").forGetter(SinPrediction::player),
            Uuids.CODEC.fieldOf("sin").forGetter(SinPrediction::sin)).apply(instance, SinPrediction::new));

    @Override
    public String toString() {
        return "Sin: "  + sin.toString();
    }

    @Override
    public void tick(World world) {
        if (((ServerWorld) world).getEntity(sin) == null) {
            PredictionData.deletePrediction(player);
        }
    }


    public static SinPrediction create(PlayerEntity playerEntity, CrystalBallBlockEntity blockEntity) {
        ServerStatHandler statHandler = ((ServerPlayerEntity) playerEntity).getStatHandler();;
        if (((StatTypeAccessor)Stats.KILLED).getStats().values().size() == 0) {
            return null;
        }
        Stat<Object> stat = Collections.max(((StatTypeAccessor)Stats.KILLED).getStats().values(), Comparator.comparing((s) -> s.getValue() != CrystalFortunes.SIN_ENTITY ? statHandler.getStat(s) : -1));
        if (statHandler.getStat(stat) == 0) {
            return null;
        }
        ServerWorld world = ((ServerPlayerEntity) playerEntity).getServerWorld();
        SinEntity entity = new SinEntity(world, (EntityType) stat.getValue(), playerEntity);
        entity.initialize(world, world.getLocalDifficulty(blockEntity.getPos()), SpawnReason.EVENT, null);

        entity.setPosition(playerEntity.getPos().add(XZ_DISTANCE_MIN+Prediction.random.nextDouble()*(XZ_DISTANCE_MAX-XZ_DISTANCE_MIN), Y_DISTANCE_MIN+Prediction.random.nextDouble()*(Y_DISTANCE_MAX-Y_DISTANCE_MIN),XZ_DISTANCE_MIN+Prediction.random.nextDouble()*(XZ_DISTANCE_MAX-XZ_DISTANCE_MIN)));
        ((ServerPlayerEntity) playerEntity).getServerWorld().spawnEntity(entity);
        return new SinPrediction(playerEntity.getUuid(), entity.getUuid());
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.SIN;
    }
}
