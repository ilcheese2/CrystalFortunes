package io.github.ilcheese2.crystal_fortunes.items;

import io.github.ilcheese2.crystal_fortunes.entities.HolyGrenadeEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class HolyGrenadeItem extends Item implements ProjectileItem {

    public HolyGrenadeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            HolyGrenadeEntity holyGrenadeEntity = new HolyGrenadeEntity(world, user);
            holyGrenadeEntity.setItem(itemStack);
            holyGrenadeEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
            world.spawnEntity(holyGrenadeEntity);
        }
        itemStack.decrementUnlessCreative(1, user);
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        HolyGrenadeEntity holyGrenadeEntity = new HolyGrenadeEntity(world, pos.getX(), pos.getY(), pos.getZ());
        holyGrenadeEntity.setItem(stack);
        return holyGrenadeEntity;
    }
}
