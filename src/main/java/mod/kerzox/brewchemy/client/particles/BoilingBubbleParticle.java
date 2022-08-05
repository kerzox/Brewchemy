package mod.kerzox.brewchemy.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Encoder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.client.particle.ParticleRenderType.PARTICLE_SHEET_OPAQUE;

public class BoilingBubbleParticle extends TextureSheetParticle {

    public BoilingBubbleParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return PARTICLE_SHEET_OPAQUE;
    }

    public static class BoilingBubbleType extends ParticleType<BoilingBubbleType> implements ParticleOptions {

        private final static
        Deserializer<BoilingBubbleType> DESERIALIZER = new Deserializer<>() {
            @Override
            public BoilingBubbleType fromCommand(ParticleType<BoilingBubbleType> pParticleType, StringReader pReader) throws CommandSyntaxException {
                return (BoilingBubbleType) pParticleType;
            }

            @Override
            public BoilingBubbleType fromNetwork(ParticleType<BoilingBubbleType> pParticleType, FriendlyByteBuf pBuffer) {
                return (BoilingBubbleType) pParticleType;
            }
        };

        private final Codec<BoilingBubbleType> codec = Codec.unit(this::getType);

        public BoilingBubbleType(boolean pOverrideLimiter) {
            super(pOverrideLimiter, DESERIALIZER);
        }


        @Override
        public @NotNull BoilingBubbleType getType() {
            return this;
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf pBuffer) {

        }

        @Override
        public String writeToString() {
            return Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this)).toString();
        }

        @Override
        public Codec<BoilingBubbleType> codec() {
            return this.codec;
        }
    }

    public static class Provider implements ParticleProvider<BoilingBubbleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(BoilingBubbleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            BoilingBubbleParticle bubblecolumnupparticle = new BoilingBubbleParticle(pLevel, pX, pY, pZ);
            bubblecolumnupparticle.pickSprite(this.sprite);
            return bubblecolumnupparticle;
        }
    }

}
