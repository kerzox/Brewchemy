package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class FluidStorageTank extends FluidTank {

    public FluidStorageTank(int capacity) {
        super(capacity);
    }

    public FluidStorageTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

}
