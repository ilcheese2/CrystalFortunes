package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.camera.CameraData;
import io.github.ilcheese2.crystal_fortunes.camera.ServerCameraHandler;
import io.github.ilcheese2.crystal_fortunes.mixin.SpawnHelperInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public record EvilBeastPrediction(UUID player, UUID rabbit) implements Prediction {


    private final static int SPAWN_DISTANCE = 100;

    public static final MapCodec<EvilBeastPrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Uuids.CODEC.fieldOf("player").forGetter(EvilBeastPrediction::player),Uuids.CODEC.fieldOf("rabbit").forGetter(EvilBeastPrediction::rabbit)).apply(instance, EvilBeastPrediction::new));

    @Override
    public String toString() {
        return "Entity: " + rabbit;
    }

    @Override
    public void tick(World world) {
        Entity rabbit2 = ((ServerWorld) world).getEntity(rabbit);
        if (rabbit2 == null) {
            PlayerEntity player = ((PlayerEntity) ((ServerWorld) world).getEntity(this.player));
            if (player != null) {
                ((ServerPlayerEntity) player).setCameraEntity(player);
                player.giveItemStack(new ItemStack(CrystalFortunes.HOLY_GRENADE, 1));
                CameraData.fixPlayer((ServerPlayerEntity) player);
            }
            PredictionData.deletePrediction(this.player);
        }
    }


    public static EvilBeastPrediction create(PlayerEntity playerEntity, BlockPos pos) {
        ServerWorld world = (ServerWorld) playerEntity.getWorld();
        RabbitEntity rabbit = new RabbitEntity(EntityType.RABBIT, world);
        rabbit.initialize(world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null);
        double x = playerEntity.getX() + (double) Prediction.random.nextInt(2*SPAWN_DISTANCE) / 2 - SPAWN_DISTANCE;
        double z = playerEntity.getZ() + (double) Prediction.random.nextInt(2*SPAWN_DISTANCE) / 2 - SPAWN_DISTANCE;
        Vec3d pos2  =Vec3d.of(SpawnHelperInvoker.invokeGetEntitySpawnPos(world, EntityType.RABBIT, (int) x, (int) z));
        rabbit.setPosition(pos2);
        rabbit.setVariant(RabbitEntity.RabbitType.EVIL);
        world.spawnEntity(rabbit);
        rabbit.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, -1, 1, true, false));
        rabbit.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, -1, 2, true, false));
        ServerCameraHandler.setCameraEntity((ServerPlayerEntity) playerEntity, rabbit);
        return new EvilBeastPrediction(playerEntity.getUuid(), rabbit.getUuid());
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.EVIL_BEAST;
    }
}
