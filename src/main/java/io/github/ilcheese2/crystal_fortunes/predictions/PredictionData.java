package io.github.ilcheese2.crystal_fortunes.predictions;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.*;

public class PredictionData extends PersistentState {

    public Map<UUID, Prediction> predictions;
    private static final UnboundedMapCodec<UUID, Prediction> predictionsCodec = Codec.unboundedMap(Uuids.CODEC, PredictionType.CODEC);
    private static boolean created = false; // shitty code
    private static final List<UUID> playersToRemove = new ArrayList<>();

    private static final Type<PredictionData> type = new Type<>(
            PredictionData::new,
            PredictionData::createFromNbt,
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

   PredictionData() {
       predictions = new HashMap<>();
   }

    public static PredictionData getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        PredictionData state = persistentStateManager.getOrCreate(type, CrystalFortunes.MODID+ "_predictions");
        //CrystalFortunes.LOGGER.info(state.predictions.toString());
        if (created) {
            for (Map.Entry<UUID, Prediction> entry : state.predictions.entrySet()) {
                if (server.getPlayerManager().getPlayer(entry.getKey()) != null) {
                    server.getPlayerManager().getPlayer(entry.getKey()).networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new PredictionPayload(entry.getValue())));
                }
            }
            state.markDirty();
            created = false;
        }
        return state;
    }

    public static Prediction hasPrediction(PlayerEntity player) {
        PredictionData serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));
        return serverState.predictions.getOrDefault(player.getUuid(), null);
    }

    public static Prediction getPrediction(PlayerEntity player, BlockPos pos) {
        PredictionData serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));
        Prediction prediction = serverState.predictions.computeIfAbsent(player.getUuid(), (uuid) -> Prediction.generatePrediction(player, pos));
        serverState.markDirty();
        return prediction;
    }

    public static Prediction setPrediction(PlayerEntity player, PredictionType<?> type) {
        PredictionData serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));
        Prediction prediction = null;
        if (!serverState.predictions.containsKey(player.getUuid())) {
            prediction = type.factory().create(player, player.getBlockPos());
            if (prediction == null) {
                return null;
            }
            serverState.predictions.put(player.getUuid(), prediction);
            ((ServerPlayerEntity) player).networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new PredictionPayload(prediction)));
        }
        serverState.markDirty();
        return prediction;
    }

    public static void deleteAllPredictions(World world) {
       PredictionData serverState = getServerState(Objects.requireNonNull(world.getServer()));
       playersToRemove.addAll(serverState.predictions.keySet());
    }

    public static void deletePrediction(UUID uuid) {
        playersToRemove.add(uuid);
    }

    public static void clearPredictionsToBeDeleted(World world) {
        PredictionData serverState = getServerState(Objects.requireNonNull(world.getServer()));
        for (UUID player: playersToRemove) {
            serverState.predictions.remove(player);
            if ( world.getServer().getPlayerManager().getPlayer(player) != null) {
                (world.getServer().getPlayerManager().getPlayer(player)).networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new PredictionPayload(new NullPrediction())));
            }
        }
        playersToRemove.clear();
        serverState.markDirty();
    }

    public static PredictionData createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        PredictionData data = new PredictionData();
        data.predictions = Maps.newHashMap(predictionsCodec.parse(NbtOps.INSTANCE, tag).result().orElse((new HashMap<>()))); // ineffiecent or something idk
        created = true;
        return data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        return (NbtCompound) predictionsCodec.encodeStart(NbtOps.INSTANCE, predictions).result().get();
    }
}
