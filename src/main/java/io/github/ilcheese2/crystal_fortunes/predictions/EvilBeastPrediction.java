package io.github.ilcheese2.crystal_fortunes.predictions;

import com.ibm.icu.impl.CacheValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.camera.ServerCameraHandler;
import io.github.ilcheese2.crystal_fortunes.mixin.SpawnHelperInvoker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.SpectateCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public record EvilBeastPrediction(UUID player, UUID entity, UUID foxEntity) implements Prediction {


    private final static int SPAWN_DISTANCE = 100;

    public static final MapCodec<EvilBeastPrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Uuids.CODEC.fieldOf("player").forGetter(EvilBeastPrediction::player),Uuids.CODEC.fieldOf("entity").forGetter(EvilBeastPrediction::entity),Uuids.CODEC.fieldOf("fox").forGetter(EvilBeastPrediction::foxEntity)).apply(instance, EvilBeastPrediction::new));

    @Override
    public String toString() {
        return "Entity: " + entity;
    }

    @Override
    public void tick(World world) {
        Entity rabbit = ((ServerWorld) world).getEntity(entity);
        if (rabbit == null) {
            PlayerEntity player = ((PlayerEntity) ((ServerWorld) world).getEntity(this.player));
            if (player != null) {
                player.giveItemStack(new ItemStack(CrystalFortunes.HOLY_GRENADE, 1));
            }
            PredictionData.deletePrediction(this.player);
        }
    }


    public static EvilBeastPrediction create(PlayerEntity playerEntity, CrystalBallBlockEntity blockEntity) {
        ServerWorld world = (ServerWorld) blockEntity.getWorld();
        RabbitEntity rabbit = new RabbitEntity(EntityType.RABBIT, world);
        rabbit.initialize(world, world.getLocalDifficulty(blockEntity.getPos()), SpawnReason.EVENT, null);
        double x = playerEntity.getX() + (double) Prediction.random.nextInt(2*SPAWN_DISTANCE) / 2 - SPAWN_DISTANCE;
        double z = playerEntity.getZ() + (double) Prediction.random.nextInt(2*SPAWN_DISTANCE) / 2 - SPAWN_DISTANCE;
        Vec3d pos  =Vec3d.of(SpawnHelperInvoker.invokeGetEntitySpawnPos(world, EntityType.RABBIT, (int) x, (int) z));
        rabbit.setPosition(pos);
        rabbit.setVariant(RabbitEntity.RabbitType.EVIL);
        FoxEntity fox = new FoxEntity(EntityType.FOX, world);
        fox.initialize(world, world.getLocalDifficulty(blockEntity.getPos()), SpawnReason.EVENT, null);
        fox.setPosition(pos);
        world.spawnEntity(rabbit);
        world.spawnEntity(fox);
        rabbit.setTarget(fox);
        rabbit.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, -1, 1, true, false));
        rabbit.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, -1, 2, true, false));
        fox.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, -1, 4, true, false));
        fox.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, -1, 3, true, false));
        fox.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, -1, 10, true, false)); // rabbit must win fox must
        ServerCameraHandler.setCameraEntity((ServerPlayerEntity) playerEntity, rabbit);
        return new EvilBeastPrediction(playerEntity.getUuid(), rabbit.getUuid(), fox.getUuid());
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.EVIL_BEAST;
    }
}
