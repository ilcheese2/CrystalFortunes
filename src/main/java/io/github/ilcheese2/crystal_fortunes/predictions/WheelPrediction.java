package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.networking.UpdateWheelPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.wacky_wheel.WheelOfWacky;
import net.hyper_pigeon.wacky_wheel.block.entity.WackyWheelBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public record WheelPrediction(UUID player, WackyWheelBlockEntity wheel) implements Prediction {

    private static PredictionType<WheelPrediction> WHEEL;

    public static void register() {
        WHEEL = PredictionType.register("wheel", new PredictionType<>(WheelPrediction.CODEC, WheelPrediction::create));
    }

    static class FakeWrapper implements RegistryWrapper.WrapperLookup {
        @Override
        public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
            return null;
        }

        @Override
        public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
            return Optional.empty();
        }
    }

    public static final MapCodec<WheelPrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Uuids.CODEC.fieldOf("player").forGetter(WheelPrediction::player)
            ,NbtCompound.CODEC.fieldOf("wheel").forGetter((wheelPrediction -> wheelPrediction.wheel.createNbt(new FakeWrapper())))).apply(instance, WheelPrediction::fromNbt));

    public static WheelPrediction create(PlayerEntity player, BlockPos pos) {
        var entity = new WackyWheelBlockEntity(BlockPos.ORIGIN, WheelOfWacky.WACKY_WHEEL_BLOCK.getDefaultState()) {
            @Override
            public void markDirty() {} // only good part of java
        };
        entity.setWorld(player.getWorld());
        if (entity.getWedgeSpells().isEmpty()) {
            entity.initWedgeSpells();
        }
        entity.spin((ServerPlayerEntity) player);
        return new WheelPrediction(player.getUuid(), entity);
    }

    static WheelPrediction fromNbt(UUID player, NbtCompound nbt) {
        var entity = new WackyWheelBlockEntity(BlockPos.ORIGIN, WheelOfWacky.WACKY_WHEEL_BLOCK.getDefaultState()) {
            @Override
            public void markDirty() {}
        };
        entity.read(nbt, new FakeWrapper()); // technically not needed since it'll get killed anyway
        return new WheelPrediction(player, entity);
    }

    @Override
    public String toString() {
        return wheel.toString();
    }

    @Override
    public void tick(World world) {
        if (wheel.getWorld() == null) {
            wheel.setWorld(world);
        }
        WackyWheelBlockEntity.serverTick(world, wheel.getPos(), wheel.getCachedState(), wheel);
        if (!wheel.getSpellFlag()) {
            PredictionData.deletePrediction(player);
        }
        PlayerEntity playerEntity = world.getPlayerByUuid(player);
        if (playerEntity != null) {
            ((ServerPlayerEntity) playerEntity).networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new UpdateWheelPayload(new Pair<>(wheel.getRoll(), wheel.getPreviousRoll()))));
        }
    }

    @Override
    public void cleanup(World world) {
    }

    @Override
    public PredictionType<?> getType() {
        return WHEEL;
    }
}

