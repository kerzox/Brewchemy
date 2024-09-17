package mod.kerzox.brewchemy.common.capabilities;

import net.minecraft.nbt.CompoundTag;

public interface ICapabilitySerializer {

    CompoundTag serialize();
    void deserialize(CompoundTag tag);

}
