package io.github.ilcheese2.crystal_fortunes.entities;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.mixin.WorldInvoker;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class SinEntity<T extends LivingEntity> extends HostileEntity {

    public EntityType<T> appearanceType = null;
    private static final TrackedData<String> ENTITY_TYPE = DataTracker.registerData(SinEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Optional<UUID>> SINNER = DataTracker.registerData(SinEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public SinEntity(EntityType<SinEntity> entityType, World world) {
        super(CrystalFortunes.SIN_ENTITY, world);
        this.moveControl = new SinEntity<T>.SinMoveControl(this);
        this.calculateDimensions();
        this.calculateBoundingBox();
        this.goalSelector.add(4, new SinEntity<T>.ChargeTargetGoal()); // this is embarrassing
        this.goalSelector.add(8, new SinEntity<T>.LookAtTargetGoal());
    }

    private boolean charging = false;
    private UUID sinner;

    public SinEntity(World world, EntityType<T> type1, PlayerEntity player) {
        super(CrystalFortunes.SIN_ENTITY, world);
        this.appearanceType = type1;
        sinner = player.getUuid();
        this.setTarget(player);
        this.dataTracker.set(ENTITY_TYPE, EntityType.getId(appearanceType).toString());
        this.dataTracker.set(SINNER, Optional.of(sinner));
        this.moveControl = new SinEntity<T>.SinMoveControl(this);
        this.calculateDimensions();
        this.calculateBoundingBox();
        this.goalSelector.add(4, new SinEntity<T>.ChargeTargetGoal()); // this is embarrassing
        this.goalSelector.add(8, new SinEntity<T>.LookAtTargetGoal());
        //this.targetSelector.add(2, new SinEntity<T>.ReturnToTargetGoal(this));
    }

    class ChargeTargetGoal
            extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = SinEntity.this.getTarget();
            if (livingEntity != null && livingEntity.isAlive() && !SinEntity.this.getMoveControl().isMoving() && SinEntity.this.random.nextInt(SinEntity.ChargeTargetGoal.toGoalTicks(4)) == 0) {
                return SinEntity.this.squaredDistanceTo(livingEntity) > 4.0;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return SinEntity.this.getMoveControl().isMoving() && SinEntity.this.charging && SinEntity.this.getTarget() != null && SinEntity.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity livingEntity = SinEntity.this.getTarget();
            if (livingEntity != null) {
                Vec3d vec3d = livingEntity.getEyePos();
                SinEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
            SinEntity.this.charging = true;
            SinEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0f, 1.0f);
        }

        @Override
        public void stop() {
            SinEntity.this.charging = false;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = SinEntity.this.getTarget();
            if (livingEntity == null) {
                return;
            }
            //VexEntity
            if (SinEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                SinEntity.this.tryAttack(livingEntity);
                SinEntity.this.charging = false;
            } else {
                double d = SinEntity.this.squaredDistanceTo(livingEntity);
                if (d < 9.0) {
                    Vec3d vec3d = livingEntity.getEyePos();
                    SinEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
                }
            }
        }
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.appearanceType == null) {
            return super.getBaseDimensions(pose);
        }
        return appearanceType.getDimensions();
    }

    class LookAtTargetGoal
            extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !SinEntity.this.getMoveControl().isMoving() && SinEntity.this.random.nextInt(SinEntity.LookAtTargetGoal.toGoalTicks(7)) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos blockPos = SinEntity.this.getBlockPos();

            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(SinEntity.this.random.nextInt(15) - 7, SinEntity.this.random.nextInt(11) - 5, SinEntity.this.random.nextInt(15) - 7);
                if (!SinEntity.this.getWorld().isAir(blockPos2)) continue;
                SinEntity.this.moveControl.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
                if (SinEntity.this.getTarget() != null) break;
                SinEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class SinMoveControl //copied from vex
            extends MoveControl {
        public SinMoveControl(SinEntity owner) {
            super(owner);
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO) {
                return;
            }
            Vec3d vec3d = new Vec3d(this.targetX - SinEntity.this.getX(), this.targetY - SinEntity.this.getY(), this.targetZ - SinEntity.this.getZ());
            double d = vec3d.length();
            if (d < SinEntity.this.getBoundingBox().getAverageSideLength()) {
                this.state = MoveControl.State.WAIT;
                SinEntity.this.setVelocity(SinEntity.this.getVelocity().multiply(0.5));
            } else {
                SinEntity.this.setVelocity(SinEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                if (SinEntity.this.getTarget() == null) {
                    Vec3d vec3d2 = SinEntity.this.getVelocity();
                    SinEntity.this.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776f);
                    SinEntity.this.bodyYaw = SinEntity.this.getYaw();
                } else {
                    double e = SinEntity.this.getTarget().getX() - SinEntity.this.getX();
                    double f = SinEntity.this.getTarget().getZ() - SinEntity.this.getZ();
                    SinEntity.this.setYaw(-((float) MathHelper.atan2(e, f)) * 57.295776f);
                    SinEntity.this.bodyYaw = SinEntity.this.getYaw();
                }
            }
        }
    }

    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        if (!getWorld().isClient && this.getTarget() == null) {
            LivingEntity c = (LivingEntity) ((ServerWorld) getWorld()).getEntity(sinner);
            if (c != null) {
                this.setTarget(c);
            }
        }

        this.noClip = false;
        this.setNoGravity(true);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ENTITY_TYPE, "");
        builder.add(SINNER, Optional.empty());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (ENTITY_TYPE.equals(data)) {
            this.appearanceType = (EntityType<T>) EntityType.get(dataTracker.get(ENTITY_TYPE)).get();
        } else if (SINNER.equals(data)) {
            this.sinner = dataTracker.get(SINNER).orElse(null);
            LivingEntity c = (LivingEntity) ((WorldInvoker) this.getWorld()).invokeGetEntityLookup().get(sinner);
            if (c != null) {
                this.setTarget(c);
                SinEntity.this.moveControl.moveTo(c.getPos().x, c.getPos().y, c.getPos().z, 1.0);
            }
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        appearanceType = (EntityType<T>) Registries.ENTITY_TYPE.get(Identifier.of(nbt.getString("appearanceID")));
        if (nbt.contains("sinner")) {
            sinner = nbt.getUuid("sinner");
            this.dataTracker.set(SINNER, Optional.of(sinner));
        }
        this.dataTracker.set(ENTITY_TYPE, EntityType.getId(appearanceType).toString());
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("appearanceID", EntityType.getId(appearanceType).toString());
        if (sinner != null) {
            nbt.putUuid("sinner", sinner);
        }
        super.writeCustomDataToNbt(nbt);
    }

    public static DefaultAttributeContainer.Builder createSinAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6f).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 1000.0f);
    }
}
