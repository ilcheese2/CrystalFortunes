package io.github.ilcheese2.crystal_fortunes.blocks;

import com.mojang.serialization.MapCodec;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.camera.ServerCameraHandler;
import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.mixin.WorldInvoker;
import io.github.ilcheese2.crystal_fortunes.predictions.*;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.ilcheese2.crystal_fortunes.predictions.PredictionData.getPlayerPrediction;

public class CrystalBallBlock extends BlockWithEntity {

    public CrystalBallBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            if (CrystalFortunesClient.prediction instanceof NullPrediction) {
                ((ClientWorld) world).setTimeOfDay(18000);
                return ActionResult.SUCCESS;
            }

            List<Text> lines = CrystalFortunesClient.translationsLookup.get(PredictionType.PREDICTION_REGISTRY.getKey(CrystalFortunesClient.prediction.getType()).get().getValue()).get("while");
            if (!lines.isEmpty()) {
                CrystalFortunesClient.dialogueRenderer.addText(lines.get(world.random.nextInt(lines.size())));
            }

            return ActionResult.CONSUME;
        }

        Prediction prediction = getPlayerPrediction(player, (CrystalBallBlockEntity) world.getBlockEntity(pos));

        if (prediction instanceof EvilBeastPrediction beastPrediction) {
            ServerCameraHandler.setCameraEntity((ServerPlayerEntity) player, ((WorldInvoker) world).invokeGetEntityLookup().get(beastPrediction.entity()));
        } else if (prediction instanceof LovePrediction lovePrediction) {
            ServerCameraHandler.setCameraEntity((ServerPlayerEntity) player, ((WorldInvoker) world).invokeGetEntityLookup().get(lovePrediction.player()));
        }

        CrystalFortunes.LOGGER.info(getPlayerPrediction(player, (CrystalBallBlockEntity) world.getBlockEntity(pos)).toString());
        return ActionResult.CONSUME;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.isClient && !(CrystalFortunesClient.prediction instanceof NullPrediction)) {
            double d;
            double e = (double) pos.getY() - (double) (random.nextFloat() * 0.5f) + 0.6;
            double f;
            if (random.nextBoolean()) {
                if (random.nextBoolean()) {
                    d = (double) pos.getX() - (double) (random.nextFloat() * 0.1f) + 0.8;
                } else {
                    d = (double) pos.getX() + (double) (random.nextFloat() * 0.1f) + 0.1;
                }
                f = (double) pos.getZ() + (double) (random.nextFloat() * 0.8f);
            } else {
                if (random.nextBoolean()) {
                    f = (double) pos.getZ() - (double) (random.nextFloat() * 0.1f) + 0.8;
                } else {
                    f = (double) pos.getZ() + (double) (random.nextFloat() * 0.1f) + 0.1;
                }
                d = (double) pos.getX() + (double) (random.nextFloat() * 0.8f);
            }

            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.END_ROD, d, e, f, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);
            }
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.3125, 0, 0.3125, 0.6875, 0.0625, 0.6875),
                VoxelShapes.cuboid(0.375, 0.0625, 0.375, 0.625, 0.125, 0.625),
                VoxelShapes.cuboid(0.3125, 0.125, 0.3125, 0.6875, 0.5, 0.6875)
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalBallBlockEntity(pos, state);
    }
}
