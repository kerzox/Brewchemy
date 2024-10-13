package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;


public class SingleFluidInventory extends MultifluidInventory {

    private HashSet<Direction> inputSides = new HashSet<>();
    private HashSet<Direction> outputSides = new HashSet<>();

    public SingleFluidInventory(int inputCapacity, int outputCapacity) {
        super(new MultifluidTank(new FluidStorage(inputCapacity)), new MultifluidTank(new FluidStorage(outputCapacity)));
    }

    private SingleFluidInventory(MultifluidInventory.InternalWrapper[] tank) {
        super(tank);
    }

    public static SingleFluidInventory of(int capacity) {
        return of(capacity, capacity);
    }

    public static SingleFluidInventory.Simple simple(int capacity) {
        return new Simple(capacity);
    }

    public static SingleFluidInventory of(int inputCapacity, int outputCapacity) {
        return new SingleFluidInventory(inputCapacity, outputCapacity);
    }

    /**
     * Simple
     * A simple version of the single fluid inventory it only uses one tank instead of two separate tanks that handle input/output
     */

    public static class Simple extends SingleFluidInventory {

        public Simple(int capacity) {
            super(createSimpleWrapper(capacity));
        }

        private static MultifluidInventory.InternalWrapper[] createSimpleWrapper(int capacity) {
            MultifluidInventory.InternalWrapper[] internalWrappers = new InternalWrapper[2];
            // the working tank which is the outward facing tank
            internalWrappers[1] = new InternalWrapper(MultifluidTank.of(1, capacity), false);
            // the input wrapper
            internalWrappers[0] = new InternalWrapper(internalWrappers[1].get(), true);
            return internalWrappers;
        }

        // ignore the input tank as its the same tank as the output

        @Override
        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            //tag.put("input", this.inputWrapper.serialize());
            tag.put("output", this.outputWrapper.serialize());
            tag.put("io", serializeInputAndOutput());
            return tag;
        }

        @Override
        public void deserialize(CompoundTag tag) {
            //this.inputWrapper.deserialize(tag.getCompound("input"));
            this.outputWrapper.deserialize(tag.getCompound("output"));
            deserializeInputAndOutput(tag.getCompound("io"));
        }

        public FluidStack getFluidInTank() {
            return getFluidInTank(0);
        }

        public void setCapacity(int amount) {
            outputWrapper.setCapacity(0, amount);
        }

        public void setFluidStack(FluidStack fluid) {
            outputWrapper.setFluidInTank(0, fluid);
        }
    }

}
