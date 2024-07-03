package io.github.ilcheese2.crystal_fortunes.items;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.networking.DialoguePayload;
import io.github.ilcheese2.crystal_fortunes.predictions.PredictionData;
import io.github.ilcheese2.crystal_fortunes.predictions.PredictionType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import symbolics.division.honque.Honque;
import symbolics.division.honque.TheFunny;
import symbolics.division.honque.magic.Honk;
import symbolics.division.honque.render.HonqueRenderer;

import java.util.Iterator;
import java.util.List;

public class CrystalHonk implements Honk {

    private static TheFunny THE_CRYSTAL_FUNNY;

    public static void registerHonk() {
        THE_CRYSTAL_FUNNY = Registry.register(Registries.ITEM, Identifier.of(CrystalFortunes.MODID, "the_crystal_funny"), new TheFunny(new CrystalHonk()));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((content) -> content.add(THE_CRYSTAL_FUNNY));
        DispenserBlock.registerBehavior(THE_CRYSTAL_FUNNY, new ProjectileDispenserBehavior(THE_CRYSTAL_FUNNY));
    }

    @Environment(EnvType.CLIENT)
    public static void registerArmorRenderer() {
        ArmorRenderer.register(new HonqueRenderer(Identifier.of(CrystalFortunes.MODID, "the_crystal_funny")), THE_CRYSTAL_FUNNY);
    }

    @Override
    public float baseProbability() {
        return 1/100f;
    }

    @Override
    public void honk(ServerPlayerEntity player, LivingEntity entity, ItemStack itemStack, Item item) {
        this.honk(player, entity, SoundEvents.BLOCK_GLASS_BREAK);
    }

    @Override
    public void badLuck(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity, ItemStack itemStack, Item item) {
        serverPlayerEntity.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new DialoguePayload("dialogue.crystal_fortunes.honk", true)));
        PredictionData.getOrCreatePrediction(serverPlayerEntity, serverPlayerEntity.getBlockPos());
    }

    @Override
    public void veryBadLuck(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity, ItemStack itemStack, Item item) {
        serverPlayerEntity.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new DialoguePayload("dialogue.crystal_fortunes.honk", true)));
        PredictionData.getOrCreatePrediction(serverPlayerEntity, serverPlayerEntity.getBlockPos());
    }

    @Override
    public void trulyUnfortunateCircumstance(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity, ItemStack itemStack, Item item) {
        serverPlayerEntity.getWorld().getEntitiesByClass(ServerPlayerEntity.class, Box.of(serverPlayerEntity.getPos(), 5.0, 5.0, 5.0), (player) -> !PredictionData.hasPrediction(player)).forEach((player) -> {
            PredictionData.setPrediction(player, PredictionType.WINDFALL);
        });
    }
}
