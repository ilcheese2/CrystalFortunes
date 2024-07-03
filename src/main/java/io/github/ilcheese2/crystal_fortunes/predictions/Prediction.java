package io.github.ilcheese2.crystal_fortunes.predictions;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.networking.DialoguePayload;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public interface Prediction {

    PredictionType<?> getType();

    String toString();

    void tick(World world);

    void cleanup(World world);

    CheckedRandom random = new CheckedRandom(TimeHelper.SECOND_IN_NANOS); // fuck it we ball

    static Prediction generatePrediction(PlayerEntity player, BlockPos pos) {
        LightningEntity entity = new LightningEntity(EntityType.LIGHTNING_BOLT, player.getWorld());
        entity.setCosmetic(true);
        entity.setPosition(Vec3d.of(pos).add(0.5, 1,0.5));
        player.getWorld().spawnEntity(entity);

        ((ServerWorld) player.getWorld()).spawnParticles(CrystalFortunes.MAGIC_PARTICLE,(double)pos.getX() + 0.5, (double)pos.getY() + 0.3, (double)pos.getZ() + 0.5, 15*4, 1f, 0.4f, 1f, 0.004f);

        ArrayList<Map.Entry<RegistryKey<PredictionType<?>>, PredictionType<?>>> list = new ArrayList<>(PredictionType.PREDICTION_REGISTRY.getEntrySet());
        Collections.shuffle(list);
        Iterator<Map.Entry<RegistryKey<PredictionType<?>>, PredictionType<?>>> iterator = list.iterator();
        Prediction prediction = null;
        while (iterator.hasNext() && prediction == null) {
            prediction = iterator.next().getValue().factory().create(player, pos);
        }
        if (prediction != null) {
            PredictionData.sendPredictionToClient(prediction, (ServerPlayerEntity) player);
        }
        return prediction;
    }

    default String getTranslationKey() {
        return "prediction." + CrystalFortunes.MODID  + "." + PredictionType.PREDICTION_REGISTRY.getKey(getType()).get().getValue().getPath();
    }
}
