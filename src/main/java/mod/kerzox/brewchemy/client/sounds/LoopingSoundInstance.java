package mod.kerzox.brewchemy.client.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class LoopingSoundInstance extends SimpleSoundInstance {

    public LoopingSoundInstance(SoundEvent p_235109_, SoundSource p_235110_, float p_235111_, float p_235112_, RandomSource p_235113_, BlockPos p_235114_) {
        super(p_235109_, p_235110_, p_235111_, p_235112_, p_235113_, p_235114_);
        this.looping = true;
    }

    public static LoopingSoundInstance create(BlockPos pos, SoundEvent event, SoundSource source, float volume) {
        return new LoopingSoundInstance(event, source, volume, 1f, RandomSource.create(), pos);
    }
}
