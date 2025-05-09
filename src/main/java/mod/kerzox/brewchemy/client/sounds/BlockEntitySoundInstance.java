package mod.kerzox.brewchemy.client.sounds;

import mod.kerzox.brewchemy.common.blockentity.base.SyncedBlockEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class BlockEntitySoundInstance extends LoopingSoundInstance{

    private SyncedBlockEntity blockEntity;

    public BlockEntitySoundInstance(SyncedBlockEntity blockEntity, SoundEvent p_235109_, SoundSource p_235110_, float p_235111_, float p_235112_, RandomSource p_235113_) {
        super(new Vec3(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ()), p_235109_, p_235110_, p_235111_, p_235112_, p_235113_);
        this.blockEntity = blockEntity;
    }

    @Override
    public void tick() {
        if (blockEntity == null || blockEntity.isRemoved()) {
            this.stop();
        }
    }

    public static BlockEntitySoundInstance from(SyncedBlockEntity blockEntity, SoundEvent event, float volume) {
        return new BlockEntitySoundInstance(blockEntity, event, SoundSource.BLOCKS, 1f, volume, RandomSource.create());
    }

    public SyncedBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
