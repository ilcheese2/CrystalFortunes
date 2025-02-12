package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record LovePrediction(UUID player, UUID lover) implements Prediction {

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

    @Override
    public void cleanup(World world) {

    }

    public static LovePrediction create(PlayerEntity playerEntity, BlockPos pos) {
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
        return new LovePrediction(playerEntity.getUuid(), serverPlayerEntity.getUuid());
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.LOVE;
    }
}
