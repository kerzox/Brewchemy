package mod.kerzox.brewchemy.common.capabilities.fluid;

import mod.kerzox.brewchemy.common.capabilities.item.IStrictSided;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SidedFluidTank extends CombinedFluidWrapper implements IStrictSided {

    private Set<Direction> input = new HashSet<>();
    private Set<Direction> output = new HashSet<>();

    private final LazyOptional<SidedFluidTank> handler = LazyOptional.of(() -> this);

    public SidedFluidTank(int inputTank, int outputTanks) {
       super(new InputWrapper(inputTank), new OutputWrapper(outputTanks));
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

//    private static IFluidHandler[] createTanks(int inputTanks, int[] inputCapacities, int outputTanks, int[] outputCapacities) {
//        IFluidHandler[] handlers = new IFluidHandler[inputTanks+outputTanks];
//        for (int i = 0; i < inputTanks; i++) {
//            handlers[i] = new InputWrapper(inputCapacities[i]);
//        }
//        for (int i = inputTanks; i < outputTanks + inputTanks; i++) {
//            handlers[i] = new OutputWrapper(outputCapacities[i - inputTanks]);
//        }
//        return handlers;
//    }

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

    public static class InputWrapper extends FluidStorageTank {

        private LazyOptional<InputWrapper> handler = LazyOptional.of(()-> this);

        public InputWrapper(int capacity) {
            super(capacity);
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

    public static class OutputWrapper extends FluidStorageTank {

        private LazyOptional<OutputWrapper> handler = LazyOptional.of(()-> this);

        public OutputWrapper(int capacity) {
            super(capacity);
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
