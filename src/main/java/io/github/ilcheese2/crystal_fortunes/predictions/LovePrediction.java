package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.mixin.SpawnHelperInvoker;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public record LovePrediction(UUID player, UUID lover) implements Prediction {

    public static final Map<UUID,UUID> lovers = new HashMap<>();

    public static final TagKey<Item> ACCEPTABLE_GIFTS = ItemTags.FLOWERS;

    public static final MapCodec<LovePrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Uuids.CODEC.fieldOf("player").forGetter(LovePrediction::player),Uuids.CODEC.fieldOf("lover").forGetter(LovePrediction::lover)).apply(instance, LovePrediction::new));

    @Override
    public String toString() {
        return "Lover: " + lover;
    }

    @Override
    public void tick(World world) {

    }


    public static LovePrediction create(PlayerEntity playerEntity, CrystalBallBlockEntity blockEntity) {
        List<ServerPlayerEntity> playerList = playerEntity.getWorld().getServer().getPlayerManager().getPlayerList();
        if (playerList.size() < 2) {
            return null;
        }
        int ourI = playerList.indexOf((ServerPlayerEntity) playerEntity);
        int i = Prediction.random.nextInt(playerList.size() - 1);
        if (ourI == i) {
            i++;
            if (ourI == playerList.size()) {
                i = 0;
            }
        }
        ServerPlayerEntity serverPlayerEntity = playerList.get(i);
        lovers.put(playerEntity.getUuid(), serverPlayerEntity.getUuid());
        return new LovePrediction(playerEntity.getUuid(), serverPlayerEntity.getUuid());
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.LOVE;
    }
}
