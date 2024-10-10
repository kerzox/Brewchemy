package mod.kerzox.brewchemy.common.capabilities.fluid;

import mod.kerzox.brewchemy.common.capabilities.ICompoundSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultifluidTank implements IFluidHandler, ICompoundSerializer {

    private FluidStorage[] fluidTanks;

    public MultifluidTank(FluidStorage... tanks) {
        this.fluidTanks = tanks;
    }

    public MultifluidTank(Stream<FluidStorage> uStream) {
        List<FluidStorage> tanks = uStream.toList();
        this.fluidTanks = new FluidStorage[tanks.size()];
        for (int i = 0; i < tanks.size(); i++) {
            fluidTanks[i] = tanks.get(i);
        }
    }

    public static MultifluidTank of(int tanks, int capacity) {
        return new MultifluidTank(IntStream.range(0, tanks).mapToObj(i -> new FluidStorage(capacity)));
    }

    public void setCapacity(int tank, int amount) {
        fluidTanks[tank].setCapacity(amount);
    }

    public FluidStorage[] getFluidTanks() {
        return fluidTanks;
    }

    @Override
    public int getTanks() {
        return fluidTanks.length;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluidTanks[tank].getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidTanks[tank].getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return fluidTanks[tank].isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        if (action.simulate()) {
            for (int i = 0; i < getTanks(); i++) {
                if (!getFluidInTank(i).isEmpty()) {
                    if (getFluidInTank(i).isFluidEqual(resource)) {
                        return Math.min(getTankCapacity(i) - getFluidInTank(i).getAmount(), resource.getAmount());
                    }
                }
            }
            for (int i = 0; i < this.getTanks(); i++) {
                if (getFluidInTank(i).isEmpty()) {
                    return Math.min(getTankCapacity(i), resource.getAmount());
                }
            }
            return 0;
        }
        return doFill(resource);
    }

    private int doFill(FluidStack resource) {
        for (int i = 0; i < getTanks(); i++) {
            if (getFluidInTank(i).isFluidEqual(resource) || getFluidInTank(i).getFluid() == resource.getFluid()) {
                int filled = getTankCapacity(i) - getFluidInTank(i).getAmount();
                if (resource.getAmount() < filled) {
                    getFluidInTank(i).grow(resource.getAmount());
                    filled = resource.getAmount();
                } else {
                    getFluidInTank(i).setAmount((getTankCapacity(i)));
                }
                if (filled > 0) {
                    onContentsChanged();
                    return filled;
                }
            } else if (getFluidInTank(i).isEmpty()) {
                fluidTanks[i].setFluid(new FluidStack(resource, Math.min(getTankCapacity(i), resource.getAmount())));
                onContentsChanged();
                return getFluidInTank(i).getAmount();
            }
        }
        return 0;
    }

    protected void onContentsChanged() {

    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        for (int i = 0; i < getTanks(); i++) {
            if (getFluidInTank(i).isFluidEqual(resource)) {
                return drain(resource.getAmount(), i, action);
            }
        }
        return FluidStack.EMPTY;
    }

    public FluidStack drain(int maxDrain, int tank, FluidAction action) {
        int drained = maxDrain;
        if (getFluidInTank(tank).getAmount() < drained) {
            drained = getFluidInTank(tank).getAmount();
        }
        FluidStack stack = new FluidStack(getFluidInTank(tank), drained);
        if (action.execute() && drained > 0) {
            getFluidInTank(tank).shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack stack = FluidStack.EMPTY;
        for (int i = 0; i < getTanks(); i++) {
            int drained = maxDrain;
            if (getFluidInTank(i).getAmount() < drained) {
                drained = getFluidInTank(i).getAmount();
            }
            stack = new FluidStack(getFluidInTank(i), drained);
            if (action.execute() && drained > 0) {
                getFluidInTank(i).shrink(drained);
                onContentsChanged();
                return stack;
            }
            if (!stack.isEmpty()) return stack;

        }
        return stack;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        for (FluidTank tank : this.fluidTanks) {
            CompoundTag tankTag = new CompoundTag();
            list.add(tank.writeToNBT(tankTag));
        }
        nbt.put("tanks", list);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tanks")) {
            ListTag list = tag.getList("tanks", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                this.fluidTanks[i].readFromNBT(list.getCompound(i));
            }
        }
    }

    public MultifluidTank copy() {
        FluidStorage[] storage = new FluidStorage[getTanks()];
        for (int i = 0; i < getTanks(); i++) {
            storage[i] = new FluidStorage(getTankCapacity(i));
            storage[i].setFluid(getFluidInTank(i).copy());
        }
        return new MultifluidTank(storage);
    }


}
