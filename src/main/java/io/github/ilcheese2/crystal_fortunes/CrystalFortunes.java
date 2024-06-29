package io.github.ilcheese2.crystal_fortunes;

import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.blocks.CrystalBallBlock;
import io.github.ilcheese2.crystal_fortunes.blocks.HolyGrenadeBlock;
import io.github.ilcheese2.crystal_fortunes.camera.CameraData;
import io.github.ilcheese2.crystal_fortunes.entities.FairyEntity;
import io.github.ilcheese2.crystal_fortunes.entities.FallingGoldEntity;
import io.github.ilcheese2.crystal_fortunes.entities.HolyGrenadeEntity;
import io.github.ilcheese2.crystal_fortunes.entities.SinEntity;
import io.github.ilcheese2.crystal_fortunes.items.HolyGrenadeItem;
import io.github.ilcheese2.crystal_fortunes.items.RingItem;
import io.github.ilcheese2.crystal_fortunes.items.RoseGlassesItem;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import io.github.ilcheese2.crystal_fortunes.networking.UpdateWheelPayload;
import io.github.ilcheese2.crystal_fortunes.predictions.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CrystalFortunes implements ModInitializer {

    public static final String MODID = "crystal_fortunes";
    public static final Block CRYSTAL_BALL = new CrystalBallBlock(Block.Settings.create());
    public static final Block HOLY_GRENADE_BLOCK = new HolyGrenadeBlock(Block.Settings.create().nonOpaque());
    public static final BlockEntityType<CrystalBallBlockEntity> CRYSTAL_BALL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MODID, "crystal_ball"), BlockEntityType.Builder.create(CrystalBallBlockEntity::new, CRYSTAL_BALL).build());
    public static final EntityType<FallingGoldEntity> FALLING_GOLD_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MODID, "falling_gold"),EntityType.Builder.<FallingGoldEntity>create(FallingGoldEntity::new, SpawnGroup.MISC)
            .dimensions(3f, 3f).build());
    public static final EntityType<SinEntity> SIN_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MODID, "sin"),EntityType.Builder.<SinEntity>create(SinEntity::new, SpawnGroup.MONSTER).build());
    public static final EntityType<FairyEntity> FAIRY_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MODID, "fairy"),EntityType.Builder.<FairyEntity>create(FairyEntity::new, SpawnGroup.CREATURE).build());
    public static final EntityType<HolyGrenadeEntity> HOLY_GRENADE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MODID, "holy_grenade"),EntityType.Builder.<HolyGrenadeEntity>create(HolyGrenadeEntity::new, SpawnGroup.MISC).build());
    public static final Item RING =
            Registry.register(Registries.ITEM, Identifier.of(MODID, "ring"),
                    new RingItem(new Item.Settings().maxCount(1)));

    public static final Item ROSE_GLASSES =
            Registry.register(Registries.ITEM, Identifier.of(MODID, "rose_glasses"),
                    new RoseGlassesItem(new Item.Settings().maxCount(1)));
    public static final Item HOLY_GRENADE =
            Registry.register(Registries.ITEM, Identifier.of(MODID, "holy_grenade"),
                    new HolyGrenadeItem(new Item.Settings()));
    public static final Item CRYSTAL_BALL_ITEM = Registry.register(Registries.ITEM, Identifier.of(MODID, "crystal_ball"), new BlockItem(CRYSTAL_BALL, new Item.Settings()));
    public static final SimpleParticleType MAGIC_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MODID, "magic_particle"), FabricParticleTypes.simple());

    public static final boolean WHEEL_OF_WACKY_LOADED = FabricLoader.getInstance().isModLoaded("wacky_wheel");

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(SIN_ENTITY, SinEntity.createSinAttributes());
        FabricDefaultAttributeRegistry.register(FAIRY_ENTITY, FairyEntity.createFairyAttributes());
        Registry.register(Registries.BLOCK, Identifier.of(MODID, "crystal_ball"), CRYSTAL_BALL);
        Registry.register(Registries.BLOCK, Identifier.of(MODID, "holy_grenade"), HOLY_GRENADE_BLOCK);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.ENDER_EYE, HOLY_GRENADE);
            content.addAfter(Items.ELYTRA, RING);
            content.addAfter(Items.SPYGLASS, ROSE_GLASSES);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> content.addAfter(Items.END_CRYSTAL, CRYSTAL_BALL_ITEM));
        PayloadTypeRegistry.playS2C().register(PredictionPayload.PREDICTION_ID, PredictionPayload.CODEC);
        if (WHEEL_OF_WACKY_LOADED) {
            PayloadTypeRegistry.playS2C().register(UpdateWheelPayload.UPDATE_WHEEL_ID, UpdateWheelPayload.CODEC);
            WheelPrediction.register();
        }
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, Prediction>> iterator = PredictionData.getServerState(server).predictions.entrySet().iterator();
            while (iterator.hasNext()) {
               Map.Entry<UUID, Prediction> prediction = iterator.next();
               if (server.getPlayerManager().getPlayer(prediction.getKey()) != null) {
                   prediction.getValue().tick(server.getOverworld());
               }// uhh fuck me
            }
            PredictionData.clearPredictionsToBeDeleted(server.getOverworld()); // clutched up
        });
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) ->  {
            CameraData.fixPlayer(handler.getPlayer());
            Prediction prediction = PredictionData.getServerState(server).predictions.get(handler.getPlayer().getUuid());
            if (prediction != null) {
                handler.sendPacket(ServerPlayNetworking.createS2CPacket(new PredictionPayload(prediction)));
            }
            else {
                handler.sendPacket(ServerPlayNetworking.createS2CPacket(new PredictionPayload(new NullPrediction())));
            }
        }));
        UseEntityCallback.EVENT.register((player1, world, hand, entity, hitResult) -> {
            if (LovePrediction.lovers.containsKey(player1.getUuid()) && LovePrediction.lovers.get(player1.getUuid()) == entity.getUuid()) {
                if (player1.getStackInHand(hand).isIn(LovePrediction.ACCEPTABLE_GIFTS)) {
                    player1.getStackInHand(hand).decrement(1);
                    LovePrediction.lovers.remove(player1.getUuid());
                    PredictionData.deletePrediction(player1.getUuid());
                    if (!player1.getInventory().contains(new ItemStack(RING, 1))) {
                        player1.giveItemStack(new ItemStack(RING, 1));
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}
