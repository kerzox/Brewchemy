package mod.kerzox.brewchemy.client.sounds;

import mod.kerzox.brewchemy.common.blockentity.BrewingKettleBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.base.SyncedBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class BrewingKettleSoundInstance extends BlockEntitySoundInstance {

    public BrewingKettleSoundInstance(SyncedBlockEntity blockEntity) {
        super(blockEntity, BrewchemyRegistry.Sounds.BOILING_LOOP.get(), SoundSource.BLOCKS, 1f, 1f, RandomSource.create());
        this.looping = true;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
    }

    /*
        Volume is based on how hot/cold the kettle is clamped at 100
        Yes negative heat is also the boiling sound... I.. err... well... yep.
     */

    @Override
    public float getVolume() {
        int clampedHeat = Math.min(Math.abs(getBlockEntity().getHeat()), 100);
        float normaliseValue = clampedHeat / 100.0f;
        return normaliseValue * super.getVolume();
    }

    @Override
    public BrewingKettleBlockEntity getBlockEntity() {
        return (BrewingKettleBlockEntity) super.getBlockEntity();
    }
}
