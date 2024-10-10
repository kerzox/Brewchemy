package mod.kerzox.brewchemy.common.capabilities.fluid;


import mod.kerzox.brewchemy.common.capabilities.CapabilityHolder;
import mod.kerzox.brewchemy.common.capabilities.ICompoundSerializer;
import mod.kerzox.brewchemy.common.capabilities.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class MultifluidInventory extends CombinedFluidInv implements IStrictInventory<MultifluidInventory>, CapabilityHolder<MultifluidInventory>, ICompoundSerializer {

    protected HashSet<Direction> inputSides = new HashSet<>();
    protected HashSet<Direction> outputSides = new HashSet<>();
    protected MultifluidInventory.InternalWrapper inputWrapper;
    protected MultifluidInventory.InternalWrapper outputWrapper;
    protected LazyOptional<MultifluidInventory> combined = LazyOptional.of(() -> this);
    protected LazyOptional<MultifluidInventory.InternalWrapper> input;
    protected LazyOptional<MultifluidInventory.InternalWrapper> output;

    protected MultifluidInventory(InternalWrapper... tank) {
        super(tank[0]);
        this.inputWrapper = tank[0];
        this.outputWrapper = tank[1];
        this.input = LazyOptional.of(() -> this.inputWrapper);
        this.output = LazyOptional.of(() -> this.outputWrapper);
    }

    public MultifluidInventory(InternalWrapper input, InternalWrapper output) {
        super(input, output);
        this.inputWrapper = input;
        this.outputWrapper = output;
        this.input = LazyOptional.of(() -> this.inputWrapper);
        this.output = LazyOptional.of(() -> this.outputWrapper);
    }

    public MultifluidInventory(MultifluidTank input, MultifluidTank output) {
        this(new InternalWrapper(input, true), new InternalWrapper(output, false));
    }

    public static MultifluidInventory of(MultifluidTank input, MultifluidTank output) {
        return new MultifluidInventory(input, output);
    }

    @Override
    public MultifluidInventory getInstance() {
        return this;
    }

    @Override
    public Capability<?> getType() {
        return ForgeCapabilities.FLUID_HANDLER;
    }

    public InternalWrapper getInputWrapper() {
        return this.inputWrapper;
    }

    public InternalWrapper getOutputWrapper() {
        return this.outputWrapper;
    }

    public void setCapacity(int index, int tank, int amount) {
        ((InternalWrapper) getHandlerFromIndex(index)).setCapacity(tank, amount);
    }

    public @NotNull InternalWrapper getInternalHandlerFromTank(int tank) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        return (InternalWrapper) handler;
    }

    @Override
    public LazyOptional<MultifluidInventory> getCapabilityHandler(Direction side) {
        // return combined
        if (side == null) return combined.cast();
        else if (getInputs().contains(side) && getOutputs().contains(side)) return combined.cast();
            // return only input
        else if (getInputs().contains(side)) return input.cast();
            // return only output
        else if (getOutputs().contains(side)) return output.cast();
        // return empty
        return LazyOptional.empty();
    }

    @Override
    public void invalidate() {

    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("input", this.inputWrapper.serialize());
        tag.put("output", this.outputWrapper.serialize());
        tag.put("io", serializeInputAndOutput());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.inputWrapper.deserialize(tag.getCompound("input"));
        this.outputWrapper.deserialize(tag.getCompound("output"));
        deserializeInputAndOutput(tag.getCompound("io"));
    }

    @Override
    public HashSet<Direction> getInputs() {
        return this.inputSides;
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return this.outputSides;
    }

    public static class InternalWrapper implements IFluidHandler, ICompoundSerializer {

        private MultifluidTank tank;
        private boolean input;

        public InternalWrapper(MultifluidTank tank, boolean input) {
            this.tank = tank;
            this.input = input;
        }

        public int internalFill(FluidStack resource, FluidAction action) {
            return tank.fill(resource, action);
        }

        public @NotNull FluidStack internalDrain(int maxDrain, FluidAction action) {
            return tank.drain(maxDrain, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return !input ? tank.drain(maxDrain, action) : FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return !input ? tank.drain(resource, action) : FluidStack.EMPTY;
        }

        @Override
        public int getTanks() {
            return tank.getTanks();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return this.tank.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.tank.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return this.tank.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return input ? tank.fill(resource, action) : 0;
        }


        @Override
        public CompoundTag serialize() {
            return tank.serialize();
        }

        @Override
        public void deserialize(CompoundTag tag) {
            tank.deserialize(tag);
        }

        public MultifluidTank get() {
            return this.tank;
        }

        public void setCapacity(int tank, int amount) {
            this.tank.setCapacity(tank, amount);
        }
    }
}
