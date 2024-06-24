package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.entities.FairyEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FairyEntityModel extends SinglePartEntityModel<FairyEntity> {
    public static final Animation IDLE_ANIMATION = Animation.Builder.create(3.0F).looping()
            .addBoneAnimation("a", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, -30.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, -30.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, -30.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
            .addBoneAnimation("b", new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(3.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
            ))
                    .addBoneAnimation("fairy", new Transformation(Transformation.Targets.TRANSLATE,
                            new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, 0.8F, 0.0F), Transformation.Interpolations.CUBIC),
                            new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
                    )
            )
            .build();

    private final ModelPart uhhanimatestuff;
    private final ModelPart fairy;
    private final ModelPart hat;
    private final ModelPart wings;
    private final ModelPart a;
    private final ModelPart b;
    private final ModelPart body;
    private final ModelPart hair;
    public FairyEntityModel(ModelPart root) {
        this.uhhanimatestuff = root.getChild("uhhanimatestuff");
        this.fairy = uhhanimatestuff.getChild("fairy");
        this.hat = fairy.getChild("hat");
        this.wings = fairy.getChild("wings");
        this.a = wings.getChild("a");
        this.b = wings.getChild("b");
        this.body = fairy.getChild("body");
        this.hair = fairy.getChild("hair");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData uhhanimatestuff = modelPartData.addChild("uhhanimatestuff", ModelPartBuilder.create(), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData fairy = uhhanimatestuff.addChild("fairy", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData hat = fairy.addChild("hat", ModelPartBuilder.create().uv(-1, 0).cuboid(-3.5833F, 1.9391F, -4.7392F, 7.0F, 1.0F, 9.0F, new Dilation(0.0F))
                .uv(-1, 11).cuboid(-2.5833F, 1.1891F, -3.4892F, 5.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(25, 9).cuboid(-1.5833F, -1.0609F, -2.2392F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0833F, -14.1377F, -0.0108F));

        ModelPartData cube_r1 = hat.addChild("cube_r1", ModelPartBuilder.create().uv(-1, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.1667F, -3.4928F, -0.2568F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r2 = hat.addChild("cube_r2", ModelPartBuilder.create().uv(21, 35).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.1667F, -1.5037F, 0.2707F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r3 = hat.addChild("cube_r3", ModelPartBuilder.create().uv(21, 0).cuboid(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-0.0833F, 0.9292F, -0.0464F, -0.2182F, 0.0F, 0.0F));

        ModelPartData wings = fairy.addChild("wings", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData a = wings.addChild("a", ModelPartBuilder.create(), ModelTransform.of(-2.2469F, -6.6986F, 2.7286F, 0.0F, -0.3379F, 0.0F));

        ModelPartData cube_r4 = a.addChild("cube_r4", ModelPartBuilder.create().uv(29, 30).cuboid(-0.5F, -1.5F, -2.5F, 1.0F, 3.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 0.0F, -0.3079F, -0.1222F, 0.0396F));

        ModelPartData cube_r5 = a.addChild("cube_r5", ModelPartBuilder.create().uv(0, 20).cuboid(-0.5F, -2.0F, -3.5F, 1.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.3503F, -0.082F, -0.0299F));

        ModelPartData b = wings.addChild("b", ModelPartBuilder.create(), ModelTransform.of(-2.8749F, -6.6986F, -2.3419F, 0.0F, 0.3379F, 0.0F));

        ModelPartData cube_r6 = b.addChild("cube_r6", ModelPartBuilder.create().uv(29, 20).cuboid(-0.5F, -1.5F, -2.5F, 1.0F, 3.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 0.3065F, 0.1222F, 0.0263F));

        ModelPartData cube_r7 = b.addChild("cube_r7", ModelPartBuilder.create().uv(16, 13).cuboid(-0.5F, -2.0F, -3.5F, 1.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, -0.3516F, 0.0974F, -0.0175F));

        ModelPartData body = fairy.addChild("body", ModelPartBuilder.create().uv(0, 32).cuboid(-0.875F, -0.875F, -1.5F, 2.0F, 5.0F, 3.0F, new Dilation(0.0F))
                .uv(17, 25).cuboid(-0.875F, -4.875F, -2.5F, 3.0F, 4.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 20).cuboid(-0.875F, -0.875F, -2.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 11).cuboid(-0.875F, -0.875F, 1.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.375F, -6.3236F, 0.0F));

        ModelPartData hair = fairy.addChild("hair", ModelPartBuilder.create().uv(26, 11).cuboid(-0.0714F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 11).cuboid(-0.0714F, -1.5F, -2.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 24).cuboid(-0.0714F, -1.5F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 20).cuboid(-0.0714F, -1.5F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(37, 17).cuboid(-3.0714F, -1.5F, -3.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 5).cuboid(-0.0714F, -1.5F, 1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(11, 35).cuboid(-3.0714F, -1.5F, 2.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(2.5714F, -9.6986F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(FairyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        updateAnimation(entity.idleAnimationState, IDLE_ANIMATION, ageInTicks, 1.0F);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        uhhanimatestuff.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return uhhanimatestuff;
    }
}