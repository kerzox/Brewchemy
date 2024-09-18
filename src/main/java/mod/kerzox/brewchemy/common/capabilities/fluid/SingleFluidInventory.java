package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.HashSet;

public class SingleFluidInventory extends MultifluidInventory {

    private HashSet<Direction> inputSides = new HashSet<>();
    private HashSet<Direction> outputSides = new HashSet<>();

    public SingleFluidInventory(int inputCapacity, int outputCapacity) {
        super(new MultifluidTank(new FluidTank(inputCapacity)), new MultifluidTank(new FluidTank(outputCapacity)));
    }

    public static SingleFluidInventory of(int capacity) {
        return of(capacity, capacity);
    }

    public static SingleFluidInventory of(int inputCapacity, int outputCapacity) {
        return new SingleFluidInventory(inputCapacity, outputCapacity);
    }

}
