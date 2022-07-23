package mod.kerzox.brewchemy.common.capabilities.fluid;

import mod.kerzox.brewchemy.common.capabilities.item.IStrictSided;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class SidedMultifluidTank extends CombinedFluidInventory implements IStrictSided {
    private Set<Direction> input = new HashSet<>();
    private Set<Direction> output = new HashSet<>();

    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> this);

    public SidedMultifluidTank(int inputTanks, int inputCapacities, int outputTanks, int outputCapacities) {
        super(new InputWrapper(inputTanks, inputCapacities), new OutputWrapper(outputTanks, outputCapacities));
        input.addAll(Arrays.stream(Direction.values()).toList());
        output.add(Direction.NORTH);
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

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        FluidStack ret = FluidStack.EMPTY;
        for (int i = 0; i < slotCount; i++) {
            IFluidHandler handler = getHandlerFromSlot(i);
            if (handler instanceof InputWrapper input) {
                ret = input.forceDrain(resource, action);
            } else {
                ret = handler.drain(resource, action);
            }
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        return ret;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack ret = FluidStack.EMPTY;
        for (int i = 0; i < slotCount; i++) {
            IFluidHandler handler = getHandlerFromSlot(i);
            FluidStack resource = getFluidInTank(i);
            if (!resource.isEmpty()) {
                if (handler instanceof InputWrapper input) {
                    ret = input.forceDrain(resource, action);
                } else {
                    ret = handler.drain(resource, action);
                }
            }
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        return super.drain(maxDrain, action);
    }

    @Override
    public Set<Direction> getOutputs() {
        return output;
    }

    @Override
    public Set<Direction> getInputs() {
        return input;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        getInputHandler().write(tag);
        getOutputHandler().write(tag);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        getInputHandler().read(tag.getCompound("fluidHandler"));
        getOutputHandler().read(tag.getCompound("fluidHandler"));
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

        @Override
        public CompoundTag write(CompoundTag tag) {
            CompoundTag tag1 = new CompoundTag();
            tag.put("input", super.write(tag1));
            return tag;
        }

        @Override
        public void read(CompoundTag tag) {
            super.read(tag.getCompound("input"));
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

        public int forceFill(FluidStack resource, FluidAction action) {
            return super.fill(resource, action);
        }

        public <T> LazyOptional<T> getHandler() {
            return handler.cast();
        }

        @Override
        public CompoundTag write(CompoundTag tag) {
            CompoundTag tag1 = new CompoundTag();
            tag.put("output", super.write(tag1));
            return tag;
        }

        @Override
        public void read(CompoundTag tag) {
            super.read(tag.getCompound("output"));
        }
    }
}
