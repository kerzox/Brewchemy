package mod.kerzox.brewchemy.common.capabilities.fluid;

import mod.kerzox.brewchemy.common.capabilities.IStrictSided;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SidedMultifluidTank extends CombinedFluidInventory implements IStrictSided, INBTSerializable<CompoundTag> {
    private Set<Direction> input = new HashSet<>();
    private Set<Direction> output = new HashSet<>();

    private final PlayerWrapper playerWrapper = new PlayerWrapper(this);
    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> this);

    public SidedMultifluidTank(int inputTanks, int inputCapacities, int outputTanks, int outputCapacities) {
        super(new InputWrapper(inputTanks, inputCapacities), new OutputWrapper(outputTanks, outputCapacities));
        input.addAll(Arrays.asList(Direction.values()));
        output.add(Direction.NORTH);
    }

    public <T> LazyOptional<T> getHandler(Direction side) {
        if (side == null) {
            return playerWrapper.getHandler();
        } else if (getInputs().contains(side) && getOutputs().contains(side)) {
            return handler.cast();
        } else if (getInputs().contains(side)) return getInputHandler().getHandler();
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


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("input", this.getInputHandler().serializeNBT());
        nbt.put("output", this.getOutputHandler().serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("input")) {
            getInputHandler().deserializeNBT(nbt.getCompound("input"));
        }
        if (nbt.contains("output")) {
            getOutputHandler().deserializeNBT(nbt.getCompound("output"));
        }
        onLoad();
    }

    protected void onLoad() {

    }

    public static class InputWrapper extends MultitankFluid {

        private LazyOptional<InputWrapper> handler = LazyOptional.of(() -> this);

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

        private LazyOptional<OutputWrapper> handler = LazyOptional.of(() -> this);

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

    }

    public static class PlayerWrapper implements IFluidHandler, IStrictSided {

        private SidedMultifluidTank inventory;

        private LazyOptional<PlayerWrapper> handler = LazyOptional.of(() -> this);

        public PlayerWrapper(SidedMultifluidTank inventory) {
            this.inventory = inventory;
        }

        @Override
        public int getTanks() {
            return inventory.getTanks();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return inventory.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return inventory.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return inventory.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int ret = 0;
            for (int i = 0; i < inventory.slotCount; i++) {
                IFluidHandler handler = inventory.getHandlerFromIndex(i);
                if (handler instanceof OutputWrapper outputWrapper) {
                    ret = outputWrapper.forceFill(resource, action);
                } else ret = inventory.getHandlerFromSlot(i).fill(resource, action);
                if (ret != 0) {
                    return ret;
                }
            }
            return ret;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return inventory.drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            int toDrain = maxDrain;
            FluidStack ret = FluidStack.EMPTY;
            for (int i = 0; i < inventory.slotCount; i++) {
                IFluidHandler handler = inventory.getHandlerFromIndex(i);
                if (getFluidInTank(i).getAmount() < maxDrain) {
                    toDrain = getFluidInTank(i).getAmount();
                }
                FluidStack resource = new FluidStack(getFluidInTank(i), toDrain);
                if (handler instanceof InputWrapper inputWrapper) {
                    ret = inputWrapper.forceDrain(resource, action);
                } else ret = inventory.getHandlerFromSlot(i).drain(resource, action);
                if (!ret.isEmpty()) {
                    return ret;
                }
            }
            return ret;
        }

        public <T> LazyOptional<T> getHandler() {
            return handler.cast();
        }

        @Override
        public Set<Direction> getOutputs() {
            return this.inventory.output;
        }

        @Override
        public Set<Direction> getInputs() {
            return this.inventory.getInputs();
        }
    }
}
