package io.github.ilcheese2.crystal_fortunes.client;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.client.particle.MagicParticle;
import io.github.ilcheese2.crystal_fortunes.client.renderers.*;
import io.github.ilcheese2.crystal_fortunes.mixin.WorldInvoker;
import io.github.ilcheese2.crystal_fortunes.networking.PredictionPayload;
import io.github.ilcheese2.crystal_fortunes.predictions.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.EndRodBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

import java.util.*;

public class CrystalFortunesClient implements ClientModInitializer {

    public static Prediction prediction; // where to put
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
    public static final Map<Identifier, Map<String, List<Text>>> translationsLookup = new HashMap<Identifier, Map<String, List<Text>>>();

    private static List<ManagedShaderEffect> shaderRequests = new ArrayList<>();
    public static DialogueRenderer dialogueRenderer;

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
                name = ((WorldInvoker) context.client().world).invokeGetEntityLookup().get(love.lover()).getName();
            }
            if (prediction instanceof NullPrediction) {
                releaseShader(LOVE_SHADER);
                return;
            } //shrug

            List<Text> lines = translationsLookup.get(PredictionType.PREDICTION_REGISTRY.getKey(prediction.getType()).get().getValue()).get("receive");
            Text dialogue = lines.get(MinecraftClient.getInstance().world.random.nextInt(lines.size()));

            if (name != null) {
                dialogue = Text.of(dialogue.getString().replace("{name}", name.getString()));
            }
            dialogueRenderer.addText(dialogue);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (needLoveEffects()) { HeartRenderer.increaseSize(); }
            dialogueRenderer.tick();
        });

        HeartRenderer.initialize();

        HudRenderCallback.EVENT.register(((drawContext, tickCounter) -> {
            dialogueRenderer.render(drawContext);
        }));
        ShaderEffectRenderCallback.EVENT.register((tickDelta) -> {
            if (!shaderRequests.isEmpty()) {
                shaderRequests.getFirst().render(tickDelta);
            }
        });
    }

    public static void requestShader(ManagedShaderEffect shader) {
        shaderRequests.addFirst(shader);
    }

    public static void releaseShader(ManagedShaderEffect shader) {
        shaderRequests.remove(shader);
    }

    public static void buildTranslations() {
        translationsLookup.clear();
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
