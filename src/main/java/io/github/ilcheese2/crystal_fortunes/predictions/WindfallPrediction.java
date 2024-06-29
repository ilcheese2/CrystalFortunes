package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.entities.FallingGoldEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;

import java.util.UUID;

public record WindfallPrediction(UUID uuid, BlockBox safeArea) implements Prediction {

    private final static int DISTANCE = 15;

    public static final MapCodec<WindfallPrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Uuids.CODEC.fieldOf("player").forGetter(WindfallPrediction::uuid),
    BlockBox.CODEC.fieldOf("safeArea").forGetter(WindfallPrediction::safeArea)).apply(instance, WindfallPrediction::new));

    @Override
    public String toString() {
        return "SafeArea: "  + safeArea.toString();
    }

    @Override
    public void tick(World world) {
        PlayerEntity player = (world).getPlayerByUuid(uuid);

        if (player == null) {
            return;
        }

        if (!safeArea.contains(player.getBlockPos())) {
            world.spawnEntity(new FallingGoldEntity(world, player));
            PredictionData.deletePrediction(uuid);
        }
    }


    WindfallPrediction(PlayerEntity playerEntity, CrystalBallBlockEntity blockEntity) {
        this(playerEntity.getUuid(), BlockBox.create(blockEntity.getPos().add(-DISTANCE,-DISTANCE,-DISTANCE),blockEntity.getPos().add(DISTANCE,DISTANCE,DISTANCE)));
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.WINDFALL;
    }
}
