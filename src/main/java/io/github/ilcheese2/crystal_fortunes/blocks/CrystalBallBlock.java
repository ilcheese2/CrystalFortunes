package io.github.ilcheese2.crystal_fortunes.blocks;

import com.mojang.serialization.MapCodec;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.camera.ServerCameraHandler;
import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.entities.FairyEntity;
import io.github.ilcheese2.crystal_fortunes.mixin.WorldInvoker;
import io.github.ilcheese2.crystal_fortunes.predictions.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.ilcheese2.crystal_fortunes.CrystalFortunes.CRYSTAL_BALL_BLOCK_ENTITY;
import static io.github.ilcheese2.crystal_fortunes.predictions.PredictionData.getPrediction;

public class CrystalBallBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final MapCodec<CrystalBallBlock> CODEC = CrystalBallBlock.createCodec(CrystalBallBlock::new);

    public CrystalBallBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
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
                Text dialogue = lines.get(world.random.nextInt(lines.size()));
                if ( CrystalFortunesClient.prediction instanceof LovePrediction love) {
                    Entity lover = ((WorldInvoker) MinecraftClient.getInstance().world).invokeGetEntityLookup().get(love.lover());
                    if (lover != null) {
                        String name = lover.getName().getString();
                        dialogue = Text.of(dialogue.getString().replace("{name}", name));
                    }
                }
                CrystalFortunesClient.dialogueRenderer.addText(dialogue);
            }

            return ActionResult.SUCCESS;
        }

        Prediction prediction = getPrediction(player, pos);

        if (prediction instanceof EvilBeastPrediction beastPrediction) {
            ServerCameraHandler.setCameraEntity((ServerPlayerEntity) player, ((WorldInvoker) world).invokeGetEntityLookup().get(beastPrediction.rabbit()));
        } else if (prediction instanceof LovePrediction lovePrediction) {
            ServerPlayerEntity entity = (ServerPlayerEntity) ((WorldInvoker) world).invokeGetEntityLookup().get(lovePrediction.player());
            if (entity != null && entity.getCameraEntity() == entity) {
                ServerCameraHandler.setCameraEntity((ServerPlayerEntity) player, entity);
            }
        }

        //CrystalFortunes.LOGGER.info(getPlayerPrediction(player, (CrystalBallBlockEntity) world.getBlockEntity(pos)).toString());
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        FairyEntity fairy = new FairyEntity(CrystalFortunes.FAIRY_ENTITY, world, pos);
        Direction direction = state.get(FACING);
        fairy.setPosition(pos.toCenterPos().add(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()));
        fairy.setYaw(direction.getOpposite().asRotation());
        fairy.prevYaw = fairy.getYaw();
        world.spawnEntity(fairy);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type == CRYSTAL_BALL_BLOCK_ENTITY) {
            return (world1, pos, state1, be) -> CrystalBallBlockEntity.tick(world1, pos, state1, (CrystalBallBlockEntity) be);
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
       return new CrystalBallBlockEntity(pos, state);
    }
}
