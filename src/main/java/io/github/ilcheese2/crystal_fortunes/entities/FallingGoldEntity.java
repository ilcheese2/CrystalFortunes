package io.github.ilcheese2.crystal_fortunes.entities;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.mixin.FallingBlockEntityAccessor;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FallingGoldEntity extends FallingBlockEntity {

    public static final float SIZE = 5.0f;
    static final double SPAWN_HEIGHT = 75.0;
    static final int TICKS_ON_GROUND = 5;
    static final double SPEED = 0.1f;

    PlayerEntity target;
    int timer = -1;

    public FallingGoldEntity(World world, PlayerEntity target) {
        super(CrystalFortunes.FALLING_GOLD_ENTITY, world);
        this.setPosition(target.getX(), target.getY()+SPAWN_HEIGHT, target.getZ());
        this.setVelocity(Vec3d.ZERO);
        this.prevX = target.getZ();
        this.prevY = target.getY()+SPAWN_HEIGHT;
        this.prevZ = target.getZ();
        this.target = target;
        this.setYaw(this.getRandom().nextInt(90));
        setFallingBlockPos(getBlockPos());
        ((FallingBlockEntityAccessor) this).setBlock(Blocks.GOLD_BLOCK.getDefaultState());
    }

    public FallingGoldEntity(EntityType<? extends FallingBlockEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    @Override
    protected double getGravity() {
        return SPEED;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.getWorld().isClient) {
            return;
        }
        player.kill();
    }

    @Override
    public void tick() {
        ++this.timeFalling;
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.tickPortalTeleportation();
        if (!this.getWorld().isClient && (this.isAlive() || this.shouldDupe)) {
            if (timer != -1) {
                timer--;
                if (timer < 0) {
                    kill();
                }
            }
            else if (target == null || target.isRemoved()) {
                kill();
            }
            else if (isOnGround()) {
                Vec3d vec3d = getBlockPos().toCenterPos().add(0.0, 0.5, 0.0);
                int i = 200;
                ((ServerWorld )this.getWorld()).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, this.getBlockState()), vec3d.x, vec3d.y, vec3d.z, i, (SIZE-1.4f)/2, 0.3f, (SIZE-1.4f)/2, 0.15f);
                timer = TICKS_ON_GROUND;
                getWorld().spawnEntity(new FallingGoldEntity(getWorld(), target));
            }
        }
        this.setVelocity(this.getVelocity().multiply(0.98));
    }
}
