package io.github.ilcheese2.crystal_fortunes.predictions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.ilcheese2.crystal_fortunes.items.CrystalHonk;
import io.github.ilcheese2.crystal_fortunes.mixin.TheFunnyAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import symbolics.division.honque.Honque;
import symbolics.division.honque.TheFunny;

import java.util.UUID;

public record HonkPrediction(UUID player) implements Prediction{

    public static final MapCodec<HonkPrediction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Uuids.CODEC.fieldOf("player").forGetter(HonkPrediction::player)).apply(instance, HonkPrediction::new));


    private static PredictionType<HonkPrediction> HONK;

    public static void register() {
        HONK = PredictionType.register("honk", new PredictionType<>(HonkPrediction.CODEC, HonkPrediction::create));
    }

    @Override
    public PredictionType<?> getType() {
        return HONK;
    }

    @Override
    public void tick(World world) {

    }

    @Override
    public void cleanup(World world) {

    }

    private static int gcd(int a, int b) { // and then he bikeshedded all over the place
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }

    private static int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }

    public static HonkPrediction create(PlayerEntity playerEntity, BlockPos pos) {
        var items = Registries.ITEM.getEntryList(Honque.Tags.FUNNIES);

        if (items.isPresent()) {
            final int[] floatToInt = {100};
            items.get().forEach((item) -> {
                var funny = (TheFunnyAccessor) item.value();
                int chance = (int) (1 / funny.getWhatIDo().baseProbability());
                floatToInt[0] = lcm(floatToInt[0], chance);
            });
            var pool = new DataPool.Builder<TheFunny>();
            items.get().forEach((item) -> {
                if (!(((TheFunnyAccessor) item.value()).getWhatIDo() instanceof CrystalHonk)) { // it does wierd stuff with dialogue
                    pool.add((TheFunny) item.value(), (int) (((TheFunnyAccessor) item.value()).getWhatIDo().baseProbability() * floatToInt[0]));
                }
            });
            var rand = random.nextFloat();
            var serverPlayer = (ServerPlayerEntity) playerEntity;
            var itemStack = playerEntity.getEquippedStack(EquipmentSlot.HEAD);
            var funny = pool.build().getDataOrEmpty(random);
            if (funny.isPresent()) {
                if (rand < 1 / 200f) {
                    ((TheFunnyAccessor) funny.get()).getWhatIDo().trulyUnfortunateCircumstance(serverPlayer, playerEntity, itemStack, funny.get());
                } else if (rand < 1 / 5f) {
                    ((TheFunnyAccessor) funny.get()).getWhatIDo().veryBadLuck(serverPlayer, playerEntity, itemStack, funny.get());
                } else {
                    ((TheFunnyAccessor) funny.get()).getWhatIDo().badLuck(serverPlayer, playerEntity, itemStack, funny.get());
                }
            }
        }
        PredictionData.deletePrediction(playerEntity.getUuid());
        return new HonkPrediction(playerEntity.getUuid());
    }
}
