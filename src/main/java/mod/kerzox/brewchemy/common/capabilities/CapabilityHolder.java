package mod.kerzox.brewchemy.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public interface CapabilityHolder<T> {

    T getInstance();
    Capability<?> getType();
    LazyOptional<T> getCapabilityHandler(Direction direction);
    void invalidate();
}
