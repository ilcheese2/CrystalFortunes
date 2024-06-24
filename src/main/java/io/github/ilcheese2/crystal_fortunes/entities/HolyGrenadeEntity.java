package io.github.ilcheese2.crystal_fortunes.entities;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class HolyGrenadeEntity extends ThrownItemEntity {

    static final float POWER = 1 * 2 * 5;

    public HolyGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public HolyGrenadeEntity(World world, double x, double y, double z) {
        super(CrystalFortunes.HOLY_GRENADE_ENTITY, x, y, z, world);
    }

    public HolyGrenadeEntity(World world, PlayerEntity user) {
        super(CrystalFortunes.HOLY_GRENADE_ENTITY, user, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        super.onEntityHit(hitResult);
        this.discard();
        if (!getWorld().isClient) {
            this.getWorld().createExplosion(this, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z, POWER, World.ExplosionSourceType.MOB);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        this.discard();
        if (!getWorld().isClient) {
            this.getWorld().createExplosion(this, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z, POWER, World.ExplosionSourceType.MOB);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return CrystalFortunes.HOLY_GRENADE;
    }
}
