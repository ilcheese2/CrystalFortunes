package io.github.ilcheese2.crystal_fortunes.client.particle;

import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class MagicParticle extends CampfireSmokeParticle {

    MagicParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, boolean signal) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, signal);
        this.scale(3.0f);
        this.setBoundingBoxSpacing(0, 0);
        this.maxAge = 3 * 20;
        this.alpha = 0.75f;
        //this.setColor(165/256.f, 27/256.f, 245/256.f);
    }

    @Override
    public void tick() {
        super.tick();
        alpha = Math.min((maxAge-age)/(60.f),1) * 0.75f;
    }

    @Override
    protected int getBrightness(float tint) {

        return super.getBrightness(tint) | (15<<4);
    }

    public static class MagicParticleFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public MagicParticleFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            MagicParticle particle = new MagicParticle(clientWorld, d, e, f, g, h, i, true);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
