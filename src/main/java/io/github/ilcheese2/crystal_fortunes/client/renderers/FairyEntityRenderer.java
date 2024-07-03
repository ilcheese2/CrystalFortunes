package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import io.github.ilcheese2.crystal_fortunes.entities.FairyEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class FairyEntityRenderer extends MobEntityRenderer<FairyEntity, FairyEntityModel> {
    public FairyEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new FairyEntityModel(ctx.getPart(CrystalFortunesClient.FAIRY_MODEL_LAYER)), 0.2f);
    }

    @Override
    public Identifier getTexture(FairyEntity entity) {
        return Identifier.of(CrystalFortunes.MODID, "textures/entity/fairy/fairy.png");
    }
    @Nullable
    @Override
    protected RenderLayer getRenderLayer(FairyEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return super.getRenderLayer(entity, showBody, DialogueRenderer.fairyAlpha < 0xFF, showOutline);
    }

}
