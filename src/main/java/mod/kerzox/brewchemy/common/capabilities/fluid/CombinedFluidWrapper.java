package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombinedFluidWrapper implements IFluidHandler {

    protected final IFluidHandler[] fluidHandler; // the handlers
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots

    public CombinedFluidWrapper(IFluidHandler... itemHandler)
    {
        this.fluidHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++)
        {
            index += itemHandler[i].getTanks();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    @Override
    public int getTanks() {
        return slotCount;
    }

    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    protected IFluidHandler getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= fluidHandler.length)
        {
            return null;
        }
        return fluidHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        tank = getSlotFromIndex(tank, index);
        return handler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        tank = getSlotFromIndex(tank, index);
        return handler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        tank = getSlotFromIndex(tank, index);
        return handler.isFluidValid(tank, stack);
    }

    protected boolean canFillTank(int tank) {
        return true;
    }

    protected boolean canDrainTank(int tank) {
        return true;
    }


    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty())
        {
            return 0;
        }
        if (action.simulate())
        {
            for (int i = 0; i < this.slotCount; i++) {
                if (canFillTank(i)) {
                    if (!getFluidInTank(i).isEmpty()) {
                        if (getFluidInTank(i).isFluidEqual(resource)) {
                            return Math.min(getTankCapacity(i) - getFluidInTank(i).getAmount(), resource.getAmount());
                        }
                    }
                }
            }
            for (int i = 0; i < this.getTanks(); i++) {
                if (canFillTank(i)) {
                    if (getFluidInTank(i).isEmpty()) {
                        return Math.min(getTankCapacity(i), resource.getAmount());
                    }
                }
            }
            return 0;
        }
        return doFill(resource);
    }

    private int doFill(FluidStack resource) {
        for (int i = 0; i < slotCount; i++) {
            if (canFillTank(i)) {
                if (getFluidInTank(i).isEmpty()) {
                    getHandlerFromIndex(i).fill(new FluidStack(resource, Math.min((getTankCapacity(i)), resource.getAmount())), FluidAction.EXECUTE);
                    onContentsChanged();
                    return getFluidInTank(i).getAmount();
                } else if (getFluidInTank(i).isFluidEqual(resource)) {
                    int filled = (getTankCapacity(i) - getFluidInTank(i).getAmount());

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
                }
            }
        }
        return 0;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        for (int i = 0; i < slotCount; i++) {
            if (canDrainTank(i)) {
                if (getFluidInTank(i).isFluidEqual(resource)) {
                    return drain(resource, resource.getAmount(), i, action);
                }
            }
        }
        return FluidStack.EMPTY;
    }

    public FluidStack drain(FluidStack resource, int amount, int i, FluidAction action)
    {
        if (canDrainTank(i)) {
            int drained = amount;
            if (getFluidInTank(i).getAmount() < drained) {
                drained = getFluidInTank(i).getAmount();
            }
            FluidStack stack = new FluidStack(getFluidInTank(i), drained);
            if (action.execute() && drained > 0) {
                getFluidInTank(i).shrink(drained);
                onContentsChanged();
            }
            return stack;
        }
        return FluidStack.EMPTY;
    }

    public FluidStack drain(int maxDrain, int tank, FluidAction action)
    {
        if (canDrainTank(tank)) {
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
        return FluidStack.EMPTY;
    }

    private void onContentsChanged() {

    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        return FluidStack.EMPTY;
    }

}
