package mod.kerzox.brewchemy.common.capabilities.fluid;


import mod.kerzox.brewchemy.common.capabilities.CapabilityHolder;
import mod.kerzox.brewchemy.common.capabilities.ICapabilitySerializer;
import mod.kerzox.brewchemy.common.capabilities.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class MultifluidInventory extends CombinedFluidInv implements IStrictInventory<MultifluidInventory>, CapabilityHolder<MultifluidInventory>, ICapabilitySerializer {

    private HashSet<Direction> inputSides = new HashSet<>();
    private HashSet<Direction> outputSides = new HashSet<>();
    private MultifluidInventory.InternalWrapper inputWrapper;
    private MultifluidInventory.InternalWrapper outputWrapper;
    private LazyOptional<MultifluidInventory> combined = LazyOptional.of(() -> this);
    private LazyOptional<MultifluidInventory.InternalWrapper> input;
    private LazyOptional<MultifluidInventory.InternalWrapper> output;

    public MultifluidInventory(MultifluidTank input, MultifluidTank output) {
        super(new InternalWrapper(input, true), new InternalWrapper(output, false));
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

    @Override
    public LazyOptional<MultifluidInventory> getCapabilityHandler(Direction direction) {
        return null;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }

    @Override
    public HashSet<Direction> getInputs() {
        return this.inputSides;
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return this.outputSides;
    }

    public static class InternalWrapper extends MultifluidTank {

        private boolean input;

        public InternalWrapper(MultifluidTank tank, boolean input) {
            super(tank.getFluidTanks());
            this.input = input;
        }

        public int internalFill(FluidStack resource, FluidAction action) {
            return super.fill(resource, action);
        }

        public @NotNull FluidStack internalDrain(int maxDrain, FluidAction action) {
            return super.drain(maxDrain, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return !input ? super.drain(maxDrain, action) : FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return !input ? super.drain(resource, action) : FluidStack.EMPTY;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return input ? super.fill(resource, action) : 0;
        }
    }
}
