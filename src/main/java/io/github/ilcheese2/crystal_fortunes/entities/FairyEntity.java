package io.github.ilcheese2.crystal_fortunes.entities;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.blocks.CrystalBallBlock;
import io.github.ilcheese2.crystal_fortunes.networking.DialoguePayload;
import io.github.ilcheese2.crystal_fortunes.predictions.PredictionData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class FairyEntity extends PassiveEntity {

    public final AnimationState idleAnimationState = new AnimationState();

    private BlockPos crystalBallPos;

    HashMap<UUID, Integer> dialogueProgress = new HashMap<>();

    public FairyEntity(EntityType<? extends PassiveEntity> entityType, World world, BlockPos crystalBall) {
        super(entityType, world);
        if (world.isClient) {
            idleAnimationState.start(this.age);
        }
        this.crystalBallPos = crystalBall;
    }

    public FairyEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
        if (world.isClient) {
            idleAnimationState.start(this.age);
        }
        this.crystalBallPos = null;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        if (!PredictionData.hasPrediction(player)) {
            dialogueProgress.put(player.getUuid(), 0);
            ((ServerPlayerEntity) player).networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new DialoguePayload("dialogue.crystal_fortunes.predictionless", true)));
            return ActionResult.SUCCESS;
        }

        int progress = dialogueProgress.getOrDefault(player.getUuid(), 0) + 1;
        dialogueProgress.put(player.getUuid(),  progress);
        ((ServerPlayerEntity) player).networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new DialoguePayload("dialogue.crystal_fortunes.clear." + progress, false)));
        if (progress == 4) {
            dialogueProgress.remove(player.getUuid());
            PredictionData.deletePrediction(player.getUuid());
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public static DefaultAttributeContainer.Builder createFairyAttributes() {
        return MobEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient) {
            if (crystalBallPos != null && !getWorld().getBlockState(crystalBallPos).isOf(CrystalFortunes.CRYSTAL_BALL)) {
                this.kill();
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!getWorld().isClient) {
            if (crystalBallPos != null) {
                Block block = getWorld().getBlockState(crystalBallPos).getBlock();
                if (block instanceof CrystalBallBlock) {
                    getWorld().breakBlock(crystalBallPos , true);
                }
            }
        }
        super.onDeath(damageSource);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        crystalBallPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("crystalPos")).result().orElse(null);
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (crystalBallPos != null) {
            var result = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, crystalBallPos);
            if (result.isSuccess()) {
                nbt.put("crystalPos", result.result().get());
            }
        }
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
    }

    @Override
    public boolean isInvulnerable() {
        return false; // why would you want to kill it
    }
}
