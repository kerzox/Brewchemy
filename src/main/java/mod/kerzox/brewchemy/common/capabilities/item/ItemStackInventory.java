package mod.kerzox.brewchemy.common.capabilities.item;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ItemStackInventory extends CombinedInvWrapper implements IStrictSided, INBTSerializable<CompoundTag> {

    private Set<Direction> input = new HashSet<>();
    private Set<Direction> output = new HashSet<>();

    private final LazyOptional<ItemStackInventory> handler = LazyOptional.of(() -> this);

    public ItemStackInventory(int inputSlots, int outputSlots) {
        super(new InputHandler(inputSlots), new OutputHandler(outputSlots));
    }

    public ItemStackInventory(InputHandler inputHandler, OutputHandler outputHandler) {
        super(inputHandler, outputHandler);
    }

    public ItemStack getStackFromInputHandler(int slot) {
        return getInputHandler().getStackInSlot(slot);
    }

    public ItemStack getStackFromOutputHandler(int slot) {
        return getOutputHandler().getStackInSlot(slot);
    }

    public <T> LazyOptional<T> getHandler(Direction side) {
        if (getInputs().contains(side) && getOutputs().contains(side) || side == null) {
            return handler.cast();
        }
        if (getInputs().contains(side)) return getInputHandler().getHandler();
        else if (getOutputs().contains(side)) return getOutputHandler().getHandler();
        return LazyOptional.empty();
    }

    public InputHandler getInputHandler() {
        return (InputHandler) getHandlerFromIndex(0);
    }

    public OutputHandler getOutputHandler() {
        return (OutputHandler) getHandlerFromIndex(1);
    }

    @Override
    protected IItemHandlerModifiable getHandlerFromIndex(int index) {
        return super.getHandlerFromIndex(index);
    }

    @Override
    protected int getSlotFromIndex(int slot, int index) {
        return super.getSlotFromIndex(slot, index);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        if (handler instanceof InputHandler inputHandler) return inputHandler.forceExtractItem(slot, amount, simulate);
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public Set<Direction> getOutputs() {
        return output;
    }

    @Override
    public Set<Direction> getInputs() {
        return input;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.put("input", this.getInputHandler().serializeNBT());
        nbt.put("output", this.getOutputHandler().serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
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

    public static class InputHandler extends ItemStackHandler {

        private LazyOptional<InputHandler> handler = LazyOptional.of(()-> this);

        public InputHandler(int slots) {
            super(slots);
        }

        public @NotNull ItemStack forceExtractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        // don't allow extraction

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        public <T> LazyOptional<T> getHandler() {
            return handler.cast();
        }

        public int isFull() {
            for (int i = 0; i < getSlots(); i++) {
                if (!isSlotFull(i)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean isSlotFull(int slot) {
            return this.getStackInSlot(slot).getCount() == this.getSlotLimit(0);
        }

    }

    public static class OutputHandler extends ItemStackHandler {

        private LazyOptional<OutputHandler> handler = LazyOptional.of(()-> this);

        public OutputHandler(int slots) {
            super(slots);
        }

        public @NotNull ItemStack forceInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        // don't allow input

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        public <T> LazyOptional <T> getHandler() {
            return handler.cast();
        }

        public int isFull() {
            for (int i = 0; i < getSlots(); i++) {
                if (!isSlotFull(i)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean isSlotFull(int slot) {
            return this.getStackInSlot(slot).getCount() == this.getSlotLimit(0);
        }
    }

}
