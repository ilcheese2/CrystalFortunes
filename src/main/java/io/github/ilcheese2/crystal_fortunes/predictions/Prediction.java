package io.github.ilcheese2.crystal_fortunes.predictions;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.world.World;

import java.util.*;

public interface Prediction {

    PredictionType<?> getType();

    String toString();

    void tick(World world);

    CheckedRandom random = new CheckedRandom(TimeHelper.SECOND_IN_NANOS); // fuck it we ball

    static Prediction generatePrediction(PlayerEntity player, CrystalBallBlockEntity blockEntity) {
        LightningEntity entity = new LightningEntity(EntityType.LIGHTNING_BOLT, blockEntity.getWorld());
        entity.setCosmetic(true);
        entity.setPosition(Vec3d.of(blockEntity.getPos()).add(0.5, 1,0.5));
        blockEntity.getWorld().spawnEntity(entity);

        ((ServerWorld) blockEntity.getWorld()).spawnParticles(CrystalFortunes.MAGIC_PARTICLE,(double)blockEntity.getPos().getX() + 0.5, (double)blockEntity.getPos().getY() + 0.3, (double)blockEntity.getPos().getZ() + 0.5, 15*4, 1f, 0.4f, 1f, 0.004f);

        ArrayList<Map.Entry<RegistryKey<PredictionType<?>>, PredictionType<?>>> list = new ArrayList<>(PredictionType.PREDICTION_REGISTRY.getEntrySet());
        Collections.shuffle(list);
        Iterator<Map.Entry<RegistryKey<PredictionType<?>>, PredictionType<?>>> iterator = list.iterator();
        Prediction prediction = null;
        while (iterator.hasNext()) {
            prediction = iterator.next().getValue().factory().create(player, blockEntity);
            if (prediction != null) {
                break;
            }
        }

        ((ServerPlayerEntity) player).networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new PredictionPayload(prediction)));
        return prediction;
    }
}
