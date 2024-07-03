package io.github.ilcheese2.crystal_fortunes;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.ilcheese2.crystal_fortunes.blockentities.CrystalBallBlockEntity;
import io.github.ilcheese2.crystal_fortunes.blocks.CrystalBallBlock;
import io.github.ilcheese2.crystal_fortunes.blocks.HolyGrenadeBlock;
import io.github.ilcheese2.crystal_fortunes.camera.CameraData;
import io.github.ilcheese2.crystal_fortunes.entities.FairyEntity;
import io.github.ilcheese2.crystal_fortunes.entities.FallingGoldEntity;
import io.github.ilcheese2.crystal_fortunes.entities.HolyGrenadeEntity;
import io.github.ilcheese2.crystal_fortunes.entities.SinEntity;
import io.github.ilcheese2.crystal_fortunes.items.CrystalHonk;
import io.github.ilcheese2.crystal_fortunes.items.HolyGrenadeItem;
import io.github.ilcheese2.crystal_fortunes.items.RingItem;
import io.github.ilcheese2.crystal_fortunes.items.RoseGlassesItem;
import io.github.ilcheese2.crystal_fortunes.networking.DialoguePayload;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import io.github.ilcheese2.crystal_fortunes.networking.UpdateWheelPayload;
import io.github.ilcheese2.crystal_fortunes.predictions.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

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
    public static final boolean HONQUE_LOADED = FabricLoader.getInstance().isModLoaded("honque");

    public static final SuggestionProvider<ServerCommandSource> AVAILABLE_PREDICTIONS = SuggestionProviders.register(Identifier.of(MODID, "predictions"), (context, builder) -> CommandSource.suggestIdentifiers(Iterables.transform(PredictionType.PREDICTION_REGISTRY.getKeys(), RegistryKey::getValue), builder));

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
        PayloadTypeRegistry.playS2C().register(DialoguePayload.DIALOGUE_ID, DialoguePayload.CODEC);

        if (WHEEL_OF_WACKY_LOADED) {
            PayloadTypeRegistry.playS2C().register(UpdateWheelPayload.UPDATE_WHEEL_ID, UpdateWheelPayload.CODEC);
            WheelPrediction.register();
        }

        if (HONQUE_LOADED) {
            CrystalHonk.registerHonk();
            HonkPrediction.register();
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
            handler.sendPacket(ServerPlayNetworking.createS2CPacket(new PredictionPayload(Objects.requireNonNullElseGet(prediction, NullPrediction::new))));
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

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("predictions")
                .then(literal("info").executes(context -> {
                    if (context.getSource().getPlayer() != null) {
                        Prediction prediction = PredictionData.getPrediction(context.getSource().getPlayer());
                        if (prediction != null) {
                            context.getSource().sendFeedback(() -> Text.of(prediction.toString()), false);
                        }
                    }
                    return 1;
                }).then(argument("player", EntityArgumentType.players()).executes(context -> {
                    EntityArgumentType.getPlayers(context, "player").forEach(player -> {
                        Prediction prediction = PredictionData.getPrediction(player);
                        if (prediction != null) {
                            context.getSource().sendFeedback(() -> Text.of(prediction.toString()), false);
                        }
                    });
                    return 1;
                })))
                .then(literal("clear").requires(source -> source.hasPermissionLevel(2)).executes(context -> {
                    if (context.getSource().getPlayer() != null) {
                        PredictionData.deletePrediction(context.getSource().getPlayer().getUuid());
                        context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.clear"), false);
                    }
                    return 1;
                }).then(argument("player", EntityArgumentType.players()).executes(context -> {
                    EntityArgumentType.getPlayers(context, "player").forEach(player -> PredictionData.deletePrediction(player.getUuid()));
                    context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.clear"), false);
                    return 1;
                })).then(literal("all").executes(context -> {
                    PredictionData.deleteAllPredictions(context.getSource().getWorld());
                    context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.clear"), false);
                    return 1;
                }))).then(literal("give").requires(source -> source.hasPermissionLevel(2)).then(argument("prediction", IdentifierArgumentType.identifier()).suggests(AVAILABLE_PREDICTIONS).executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        Identifier id = IdentifierArgumentType.getIdentifier(context, "prediction");
                        var type = PredictionType.PREDICTION_REGISTRY.get(id);
                        if (type != null) {
                            if (PredictionData.setPrediction(player, type) != null) {
                                context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.set_prediction"), false);
                            }
                            else {
                                context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.set_prediction_failed"), false);
                            }
                        }
                        else {
                            context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.invalid_prediction", id.toString()), false);
                        }
                    }
                    return 1;
                }).then(argument("player", EntityArgumentType.players()).executes(context -> {
                    Identifier id = IdentifierArgumentType.getIdentifier(context, "prediction");
                    var type = PredictionType.PREDICTION_REGISTRY.get(id);
                    if (type != null) {
                        EntityArgumentType.getPlayers(context, "player").forEach(player -> {
                            if (PredictionData.setPrediction(player, type) != null) {
                                context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.set_prediction.1", player.getName()), false);
                            }
                            else {
                                context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.set_prediction_failed.1", player.getName()), false);
                            }
                        });
                    }
                    else {
                        context.getSource().sendFeedback(() -> Text.translatable("commands.crystal_fortunes.invalid_prediction", id.toString()), false);
                    }
                    return 1;
                }))))));
    }
}
