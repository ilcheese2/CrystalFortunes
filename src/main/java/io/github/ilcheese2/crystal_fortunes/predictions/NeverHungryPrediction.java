package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.entities.FallingGoldEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

public record NeverHungryPrediction(UUID player) implements Prediction {

    static final int REQUIRED_STACKS = 15;
    static final int POTATOES_PER_TICK = 4;

    public static final MapCodec<NeverHungryPrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Uuids.CODEC.fieldOf("player").forGetter(NeverHungryPrediction::player)).apply(instance, NeverHungryPrediction::new));

    @Override
    public String toString() {
        return "Player: "  + player.toString();
    }

    @Override
    public void tick(World world) {
        PlayerEntity player = world.getPlayerByUuid(player());
        if (player == null) {
            return;
        }
        if (player.getInventory().count(Items.POTATO) >= REQUIRED_STACKS*64) {
            PredictionData.deletePrediction(this.player);
            return;
        }

        ItemStack stack = new ItemStack(Items.POTATO, POTATOES_PER_TICK);

        int occupiedSlot = player.getInventory().getOccupiedSlotWithRoomForStack(stack);
        if (occupiedSlot != -1) {
            player.getInventory().insertStack(occupiedSlot, stack);
            return;
        }
        int emptySlot = player.getInventory().getEmptySlot();
        if (emptySlot != -1) {
            player.getInventory().insertStack(stack);
            return;
        }
        for (int i = 6; i < player.getInventory().size(); i++) {
            if (!player.getInventory().getStack(player.getInventory().size() - i).isOf(Items.POTATO)) {
                player.dropItem(player.getInventory().removeStack(player.getInventory().size() - i),true);
                player.getInventory().insertStack(player.getInventory().size() - i, stack);
                break;
            }
        }
    }


    NeverHungryPrediction(PlayerEntity playerEntity, CrystalBallBlockEntity blockEntity) {
        this(playerEntity.getUuid());
    }

    @Override
    public PredictionType<?> getType() {
        return PredictionType.NEVER_HUNGRY;
    }
}
