package mod.kerzox.brewchemy.common.capabilities.fluid;

import mod.kerzox.brewchemy.common.capabilities.item.IStrictSided;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SidedMultifluidTank extends CombinedFluidWrapper implements IStrictSided {
    private Set<Direction> input = new HashSet<>();
    private Set<Direction> output = new HashSet<>();

    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> this);

    public SidedMultifluidTank(int inputTank, int inputCapacities, int outputTanks, int outputCapacities) {
        super(new InputWrapper(inputTank, inputCapacities), new OutputWrapper(outputTanks, outputCapacities));
    }

    public <T> LazyOptional<T> getHandler(Direction side) {
        if (getInputs().contains(side) && getOutputs().contains(side) || side == null) {
            return handler.cast();
        }
        if (getInputs().contains(side)) return getInputHandler().getHandler();
        else if (getOutputs().contains(side)) return getOutputHandler().getHandler();
        return LazyOptional.empty();
    }

    public InputWrapper getInputHandler() {
        return (InputWrapper) getHandlerFromIndex(0);
    }

    public OutputWrapper getOutputHandler() {
        return (OutputWrapper) getHandlerFromIndex(1);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return super.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, int amount, int tank, FluidAction action) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(tank);
        if (handler instanceof InputWrapper inputWrapper) {
            return inputWrapper.forceDrain(resource, action);
        }
        return super.drain(resource, amount, tank, action);
    }

    @Override
    public Set<Direction> getOutputs() {
        return null;
    }

    @Override
    public Set<Direction> getInputs() {
        return null;
    }

    public static class InputWrapper extends MultitankFluid {

        private LazyOptional<InputWrapper> handler = LazyOptional.of(()-> this);

        public InputWrapper(int tanks, int tankCapacities) {
            super(tanks, tankCapacities);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        public FluidStack forceDrain(FluidStack resource, FluidAction action) {
            return super.drain(resource, action);
        }

        public <T> LazyOptional<T> getHandler() {
            return handler.cast();
        }

    }

    public static class OutputWrapper extends MultitankFluid {

        private LazyOptional<OutputWrapper> handler = LazyOptional.of(()-> this);

        public OutputWrapper(int tanks, int tankCapacities) {
            super(tanks, tankCapacities);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        public <T> LazyOptional<T> getHandler() {
            return handler.cast();
        }
    }
}
