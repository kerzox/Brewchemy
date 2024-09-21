package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidStorage extends FluidTank  {

    public FluidStorage(int capacity) {
        super(capacity);
    }

    @Override
    public FluidTank setCapacity(int capacity) {
        return super.setCapacity(capacity);
    }
}
