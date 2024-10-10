package mod.kerzox.brewchemy.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.kerzox.brewchemy.common.data.BrewingKettleHeating;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class FermentationParticleType extends ParticleType<FermentationParticleType.Options>{

    public static int[] PARTICLE_COLOURS = new int[] {
            0xFFFFFFFF,  //# White
            0xFFFFE0E0,  //# Light Pink
            0xFFFFC0C0, // # Soft Pink
            0xFFFFA0A0, // # Light Red
            0xFFFF8080,  //# Red
            0xFFFF6080,  //# Orange-Red
            0xFFFFA040,  //# Orange
            0xFFFFC040,  //# Yellow-Orange
            0xFFFFFF40,  //# Yellow
            0xFFBFFF40,  //# Lime
            0xFF80FF40, // # Light Green
            0xFF40FF80,  //# Green
            0xFF40FFBF,  //# Aqua Green
            0xFF40FFFF, // # Cyan
            0xFF40BFFF, // # Sky Blue
            0xFF4080FF, // # Light Blue
            0xFF4040FF, // # Blue
            0xFF6040FF, // # Indigo
            0xFF8040FF, // # Violet
            0xFFA040FF  // # Purple
    };

    public FermentationParticleType(boolean p_123837_) {
        super(p_123837_, DESERIALIZER);
    }

    public static final ParticleOptions.Deserializer<Options> DESERIALIZER =
            new ParticleOptions.Deserializer<Options>() {
                public Options fromCommand(ParticleType<Options> type, StringReader reader)
                        throws CommandSyntaxException {
                    // You may deserialize things using the given StringReader and pass them to your
                    // particle options object if needed.
                    return new Options(reader.readInt());
                }

                public Options fromNetwork(ParticleType<Options> type, FriendlyByteBuf buf) {
                    // Similar to above, deserialize any needed info from the given buffer.
                    return new Options(buf.readInt());
                }
            };

    public static final Codec<Options> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("tint", PARTICLE_COLOURS[0]).forGetter(Options::tint)
            ).apply(instance, Options::new)
    );

    @Override
    public Codec<Options> codec() {
        return CODEC;
    }

    public static class Options implements ParticleOptions {

        private int tint;

        public Options(int tint) {
            this.tint = tint;
        }

        public int tint() {
            return this.tint;
        }

        @Override
        public ParticleType<?> getType() {
            return BrewchemyRegistry.Particles.FERMENTATION_STAGE_PARTICLE.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeInt(tint);
        }

        public String writeToString() {
            return BuiltInRegistries.PARTICLE_TYPE.getKey(BrewchemyRegistry.Particles.FERMENTATION_STAGE_PARTICLE.get()).toString();
        }
    }
}