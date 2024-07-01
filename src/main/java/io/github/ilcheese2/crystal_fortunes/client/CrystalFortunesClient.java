package io.github.ilcheese2.crystal_fortunes.client;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.client.particle.MagicParticle;
import io.github.ilcheese2.crystal_fortunes.client.renderers.*;
import io.github.ilcheese2.crystal_fortunes.mixin.WorldInvoker;
import io.github.ilcheese2.crystal_fortunes.networking.DialoguePayload;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import io.github.ilcheese2.crystal_fortunes.networking.UpdateWheelPayload;
import io.github.ilcheese2.crystal_fortunes.predictions.LovePrediction;
import io.github.ilcheese2.crystal_fortunes.predictions.NullPrediction;
import io.github.ilcheese2.crystal_fortunes.predictions.Prediction;
import io.github.ilcheese2.crystal_fortunes.predictions.PredictionType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

import java.util.*;

public class CrystalFortunesClient implements ClientModInitializer {

    public static Prediction prediction = new NullPrediction(); // where to put
    public static final ManagedShaderEffect RING_SHADER = ShaderEffectManager.getInstance()
            .manage(Identifier.of(CrystalFortunes.MODID, "shaders/post/ring.json"));
    public static final ManagedShaderEffect LOVE_SHADER = ShaderEffectManager.getInstance()
            .manage(Identifier.of(CrystalFortunes.MODID, "shaders/post/love.json"));
    public static final ManagedShaderEffect LOVE_SHADER_2 = ShaderEffectManager.getInstance()
            .manage(Identifier.of(CrystalFortunes.MODID, "shaders/post/love.json"));
    public static final ManagedShaderEffect RED_SHADER = ShaderEffectManager.getInstance()
            .manage(Identifier.of(CrystalFortunes.MODID, "shaders/post/red.json"));
    public static final ManagedCoreShader GHOST_SHADER = ShaderEffectManager.getInstance()
            .manageCoreShader(Identifier.of(CrystalFortunes.MODID, "ghost"));

    public static final EntityModelLayer FAIRY_MODEL_LAYER = new EntityModelLayer(Identifier.of(CrystalFortunes.MODID, "fairy"), "main");

    // typeofprediction -> event -> lines
    public static final Map<Identifier, Map<String, List<Text>>> translationsLookup = new HashMap<>();

    private static List<ManagedShaderEffect> shaderRequests = new ArrayList<>();
    public static DialogueRenderer dialogueRenderer;

    private boolean shadersEnabled = true;

    @Override
    public void onInitializeClient() {
        prediction = null;
        shaderRequests = new ArrayList<>();
        dialogueRenderer = new DialogueRenderer();

        EntityRendererRegistry.register(CrystalFortunes.FALLING_GOLD_ENTITY, FallingGoldEntityRenderer::new);
        EntityRendererRegistry.register(CrystalFortunes.SIN_ENTITY, SinEntityRenderer::createRenderer);
        EntityRendererRegistry.register(CrystalFortunes.FAIRY_ENTITY, FairyEntityRenderer::new);
        EntityRendererRegistry.register(CrystalFortunes.HOLY_GRENADE_ENTITY, HolyGrenadeEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(FAIRY_MODEL_LAYER, FairyEntityModel::getTexturedModelData);
        BlockRenderLayerMap.INSTANCE.putBlock(CrystalFortunes.CRYSTAL_BALL, RenderLayer.getCutout());

        ParticleFactoryRegistry.getInstance().register(CrystalFortunes.MAGIC_PARTICLE, MagicParticle.MagicParticleFactory::new);

        ClientPlayNetworking.registerGlobalReceiver(PredictionPayload.PREDICTION_ID, (payload, context) -> {
            prediction = payload.prediction();
            Text name = null;
            if (prediction instanceof LovePrediction love) {
                requestShader(LOVE_SHADER);
                Entity player = ((WorldInvoker) context.client().world).invokeGetEntityLookup().get(love.lover());
                if (player != null) {
                    name = player.getName();
                }
            }
            if (prediction instanceof NullPrediction) {
                releaseShader(LOVE_SHADER, true);
                return;
            } //shrug
            if (CrystalFortunes.WHEEL_OF_WACKY_LOADED) {
                WheelRenderer.handlePrediction(prediction);
            }

            List<Text> lines = translationsLookup.get(PredictionType.PREDICTION_REGISTRY.getKey(prediction.getType()).get().getValue()).get("receive");
            Text dialogue = lines.get(MinecraftClient.getInstance().world.random.nextInt(lines.size()));

            if (name != null) {
                dialogue = Text.of(dialogue.getString().replace("{name}", name.getString()));
            }
            dialogueRenderer.addText(dialogue);
        });

        if (CrystalFortunes.WHEEL_OF_WACKY_LOADED) {
            ClientPlayNetworking.registerGlobalReceiver(UpdateWheelPayload.UPDATE_WHEEL_ID, (payload, context) -> {
                if (WheelRenderer.wackyWheel != null) {
                    WheelRenderer.wackyWheel.setPreviousRoll(WheelRenderer.wackyWheel.getRoll());
                    WheelRenderer.wackyWheel.setRoll(payload.roll().getFirst());
                }
            });
        }

        ClientPlayNetworking.registerGlobalReceiver(DialoguePayload.DIALOGUE_ID, (payload, context) -> {
            if (payload.random()) {
                List<Text> lines = translationsLookup.get(Identifier.of(CrystalFortunes.MODID, "dialogue")).get(payload.translate());
                dialogueRenderer.addText(lines.get(MinecraftClient.getInstance().world.random.nextInt(lines.size())));
            }
            else {
                dialogueRenderer.addText(Text.translatable(payload.translate()));
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (needLoveEffects()) {
                HeartRenderer.increaseSize();
            }
            dialogueRenderer.tick();
        });

        HeartRenderer.initialize();

        HudRenderCallback.EVENT.register(((drawContext, tickCounter) -> dialogueRenderer.render(drawContext)));
        if (CrystalFortunes.WHEEL_OF_WACKY_LOADED) {
            HudRenderCallback.EVENT.register(new WheelRenderer());
        }

        ShaderEffectRenderCallback.EVENT.register((tickDelta) -> {
            if (!shaderRequests.isEmpty() && shadersEnabled) {
                shaderRequests.getFirst().render(tickDelta);
            }
        });
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            prediction = new NullPrediction();
            shaderRequests.clear();
        }));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("predictionshader")
                .executes(context -> {
                    if (shadersEnabled) {
                        context.getSource().sendFeedback(Text.translatable("commands.crystal_fortunes.enabled"));
                    } else {
                        context.getSource().sendFeedback(Text.translatable("commands.crystal_fortunes.disabled"));
                    }
                    return 1;
                }).then(ClientCommandManager.literal("enable").executes(context -> {
                    shadersEnabled = true;
                    context.getSource().sendFeedback(Text.translatable("commands.crystal_fortunes.enabled"));
                    return 1;
                })).then(ClientCommandManager.literal("disable").executes(context -> {
                    shadersEnabled = false;
                    context.getSource().sendFeedback(Text.translatable("commands.crystal_fortunes.disabled"));
                    return 1;
                })).then(ClientCommandManager.literal("reset").executes(context -> {
                    shaderRequests.clear();
                    context.getSource().sendFeedback(Text.translatable("commands.crystal_fortunes.reset_shaders"));
                    return 1;
                }))
        ));

        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            if (client.player != null) {
                var iterator = client.player.getArmorItems().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().isOf(CrystalFortunes.ROSE_GLASSES)) {
                        CrystalFortunesClient.requestShader(CrystalFortunesClient.LOVE_SHADER_2);
                    }
                }
            }
        }));
    }

    public static void requestShader(ManagedShaderEffect shader) {
        shaderRequests.addFirst(shader);
    }

    public static void releaseShader(ManagedShaderEffect shader) {
        shaderRequests.remove(shader);
    }

    public static void releaseShader(ManagedShaderEffect shader, boolean all) {
        if (all) {
            shaderRequests.removeAll(Collections.singleton(shader));
        }
        else {
            releaseShader(shader);
        }
    }

    public static void buildTranslations() {
        translationsLookup.clear();
        Map<String, List<Text>> translation2 = new HashMap<>();
        for (String type : new String[]{"predictionless"}) {
            int i = 1;
            List<Text> lines = new ArrayList<>();
            String key = "dialogue." + CrystalFortunes.MODID + "." + type;
            while (true) {
                Text line = Text.translatable(key + "." + i);
                if (!line.getString().equals(key + "." + i)) {
                    lines.add(line);
                } else {
                    break;
                }
                i++;
            }
            translation2.put(key, lines);
        }
        translationsLookup.put(Identifier.of(CrystalFortunes.MODID, "dialogue"), translation2);

        PredictionType.PREDICTION_REGISTRY.getEntrySet().forEach((entry) -> {
            Identifier id = entry.getKey().getValue();
            Map<String, List<Text>> translations = new HashMap<>();
            for (String type : new String[]{"while", "receive"}) {
                List<Text> lines = new ArrayList<>();
                int i = 1;
                String key = "prediction." + id.getNamespace() + "." + id.getPath() + "." + type;
                while (true) {
                    Text line = Text.translatable(key  + "." + i);
                    if (!line.getString().equals(key + "." + i)) {
                        lines.add(line);
                    } else {
                        break;
                    }
                    i++;
                }
                if (lines.isEmpty() && !type.equals("while")) {
                    lines.add(Text.of(key));
                }
                translations.put(type, lines);
            }
            translationsLookup.put(id, translations);
        });
    }

    public static boolean needLoveEffects() {
        return !shaderRequests.isEmpty() && (shaderRequests.getFirst() == LOVE_SHADER || shaderRequests.getFirst() == LOVE_SHADER_2);
    }
}
