package mod.kerzox.brewchemy.client.render.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.common.particle.FermentationParticleType;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

import static net.minecraft.client.particle.ParticleRenderType.PARTICLE_SHEET_OPAQUE;

public class FermentationStageParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    // First four parameters are self-explanatory. The SpriteSet parameter is provided by the
    // ParticleProvider, see below. You may also add additional parameters as needed, e.g. xSpeed/ySpeed/zSpeed.
    public FermentationStageParticle(ClientLevel level, double x, double y, double z, int tint, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.lifetime = 20 * 1;
        this.spriteSet = spriteSet;
        this.setSprite(spriteSet.get(this.age, this.lifetime));
        this.gravity = 0; // Our particle floats in midair now, because why not.
        float[] colour = RenderingUtil.covertColour(tint);
        setColor(colour[0], colour[1], colour[2]);
    }

    @Override
    public void tick() {
        // Set the sprite for the current particle age, i.e. advance the animation.
        setSpriteFromAge(spriteSet);
        // Let super handle further movement. You may replace this with your own movement if needed.
        // You may also override move() if you only want to modify the built-in movement.
        super.tick();
    }

    @Override
    public void render(VertexConsumer p_107678_, Camera p_107679_, float p_107680_) {
        super.render(p_107678_, p_107679_, p_107680_);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<FermentationParticleType.Options> {
        // A set of particle sprites.
        private final SpriteSet spriteSet;

        // The registration function passes a SpriteSet, so we accept that and store it for further use.
        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        // This is where the magic happens. We return a new particle each time this method is called!
        // The type of the first parameter matches the generic type passed to the super interface.
        @Override
        public FermentationStageParticle createParticle(FermentationParticleType.Options type, ClientLevel level,
                                       double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            // We don't use the type and speed, and pass in everything else. You may of course use them if needed.
            return new FermentationStageParticle(level, x, y, z, type.tint(), spriteSet);
        }
    }

}
