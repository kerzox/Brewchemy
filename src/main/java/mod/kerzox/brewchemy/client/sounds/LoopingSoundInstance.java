package mod.kerzox.brewchemy.client.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class LoopingSoundInstance extends AbstractTickableSoundInstance {

    public LoopingSoundInstance(Vec3 pos, SoundEvent p_235109_, SoundSource p_235110_, float p_235111_, float p_235112_, RandomSource p_235113_) {
        super(p_235109_, p_235110_, p_235113_);
        this.x = pos.x();
        this.y = pos.y();
        this.z = pos.z();
        this.volume = p_235112_;
        this.pitch = p_235111_;
        this.looping = true;

    }

    @Override
    public void tick() {

    }
}
