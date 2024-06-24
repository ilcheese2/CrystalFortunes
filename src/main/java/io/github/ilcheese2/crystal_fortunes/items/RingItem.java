package io.github.ilcheese2.crystal_fortunes.items;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.mixin.StatusEffectInstanceAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;

public class RingItem extends Item {

    @Environment(EnvType.CLIENT)

    public RingItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 20 * 10;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            CrystalFortunesClient.requestShader(CrystalFortunesClient.RING_SHADER);
        }
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.success(itemStack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (world.isClient) {
            CrystalFortunesClient.releaseShader(CrystalFortunesClient.RING_SHADER);
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        if (world.isClient) {
            CrystalFortunesClient.releaseShader(CrystalFortunesClient.RING_SHADER);
        }
        super.onStoppedUsing(stack, world, user, remainingTicks);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient && user instanceof PlayerEntity) {
            if (!user.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1, 0, true, false));
            }
            ((StatusEffectInstanceAccessor) user.getStatusEffect(StatusEffects.INVISIBILITY)).setDuration(user.getStatusEffect(StatusEffects.INVISIBILITY).getDuration() + 1);
        }
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
