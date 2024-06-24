package io.github.ilcheese2.crystal_fortunes.camera;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import io.github.ilcheese2.crystal_fortunes.predictions.LovePrediction;
import io.github.ilcheese2.crystal_fortunes.predictions.PredictionData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.GameMode;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.*;


public class CameraData extends PersistentState {

    private record PlayerState(double x, double y, double z, float pitch, float yaw, GameMode gameMode) {

        static final Codec<PlayerState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PrimitiveCodec.DOUBLE.fieldOf("x").forGetter(PlayerState::x),
                PrimitiveCodec.DOUBLE.fieldOf("y").forGetter(PlayerState::y),
                PrimitiveCodec.DOUBLE.fieldOf("z").forGetter(PlayerState::z),
                PrimitiveCodec.FLOAT.fieldOf("pitch").forGetter(PlayerState::pitch),
                PrimitiveCodec.FLOAT.fieldOf("yaw").forGetter(PlayerState::yaw),
                GameMode.CODEC.fieldOf("gamemode").forGetter(PlayerState::gameMode)).apply(instance, PlayerState::new));
        PlayerState(ServerPlayerEntity player) {
            this(player.getPos().x, player.getPos().y, player.getPos().z, player.getPitch(), player.getYaw(), player.interactionManager.getGameMode());
        }
        void updatePlayer(ServerPlayerEntity player) {
            player.setPos(x,y,z);
            player.setPitch(pitch);
            player.setYaw(yaw);
            player.changeGameMode(gameMode);
        }
    }

    private Map<UUID, PlayerState> playerStates; // this whole things is overkill
    private static final UnboundedMapCodec<UUID, PlayerState> CODEC = Codec.unboundedMap(Uuids.CODEC, PlayerState.CODEC);
    private static final Type<CameraData> type = new Type<>(
            CameraData::new,
            CameraData::createFromNbt,
            null
    );

    CameraData() {
        playerStates = new HashMap<>();
    }

    public static CameraData getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        return persistentStateManager.getOrCreate(type, CrystalFortunes.MODID+ ":camera");
    }

    public static void storePlayerData(ServerPlayerEntity player) {
        CameraData serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));
        serverState.playerStates.computeIfAbsent(player.getUuid(), (uuid) -> new PlayerState(player));
        serverState.markDirty();
    }

    public static void fixPlayer(ServerPlayerEntity player) {
        CameraData serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));
        if (!serverState.playerStates.containsKey(player.getUuid())) {
            return;
        }
        serverState.playerStates.get(player.getUuid()).updatePlayer(player);
        serverState.playerStates.remove(player.getUuid());
    }

    public static CameraData createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        CameraData data = new CameraData();
        data.playerStates = Maps.newHashMap(CODEC.parse(NbtOps.INSTANCE, tag).result().get());
        return data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        return (NbtCompound) CODEC.encodeStart(NbtOps.INSTANCE, playerStates).result().get();
    }
}
