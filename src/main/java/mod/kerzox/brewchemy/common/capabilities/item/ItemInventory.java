package mod.kerzox.brewchemy.common.capabilities.item;

import mod.kerzox.brewchemy.common.capabilities.CapabilityHolder;
import mod.kerzox.brewchemy.common.capabilities.ICapabilitySerializer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * ItemInventory
 * A Sided item stack inventory with sided input and output and a combined inventory of input/output
 */

public class ItemInventory extends CombinedInvWrapper implements IStrictCombinedItemHandler<ItemInventory>, ICapabilitySerializer, CapabilityHolder<ItemInventory> {

    private HashSet<Direction> inputSides = new HashSet<>();
    private HashSet<Direction> outputSides = new HashSet<>();
    private InternalWrapper inputWrapper;
    private InternalWrapper outputWrapper;
    private LazyOptional<ItemInventory> combined = LazyOptional.of(()-> this);
    private LazyOptional<InternalWrapper> input;
    private LazyOptional<InternalWrapper> output;

    private ItemInventory(InternalWrapper input, InternalWrapper output) {
        super(input, output);
        addInput(Direction.values());
        this.inputWrapper = input;
        this.outputWrapper = output;
        this.input = LazyOptional.of(()-> inputWrapper);
        this.output = LazyOptional.of(()-> outputWrapper);
        this.inputWrapper.setOwner(this);
        this.outputWrapper.setOwner(this);
    }

    public static ItemInventory of(int inputSlots, int outputSlots) {
        ItemInventory created = new ItemInventory(new InternalWrapper(inputSlots, true), new InternalWrapper(outputSlots, false));
        return created;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        int index = getIndexForSlot(slot);
        InternalWrapper handler = (InternalWrapper) getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.isInput() ? handler.insertItem(slot, stack, simulate) : handler.internalInsertItem(slot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        int index = getIndexForSlot(slot);
        InternalWrapper handler = (InternalWrapper) getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.isInput() ? handler.internalExtractItem(slot, amount, simulate) : handler.extractItem(slot, amount, simulate);
    }

    @Override
    public InternalWrapper getInputHandler() {
        return this.inputWrapper;
    }

    @Override
    public InternalWrapper getOutputHandler() {
        return this.outputWrapper;
    }

    @Override
    public HashSet<Direction> getInputs() {
        return inputSides;
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return outputSides;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("input", this.getInputHandler().serializeNBT());
        nbt.put("output", this.getOutputHandler().serializeNBT());
        nbt.put("strict", serializeInputAndOutput());
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        if (nbt.contains("input")) {
            getInputHandler().deserializeNBT(nbt.getCompound("input"));
        }
        if (nbt.contains("output")) {
            getOutputHandler().deserializeNBT(nbt.getCompound("output"));
        }
        if (nbt.contains("strict")) {
            deserializeInputAndOutput(nbt.getCompound("strict"));
        }
    }

    @Override
    public ItemInventory getInstance() {
        return this;
    }

    @Override
    public Capability<?> getType() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    public LazyOptional<ItemInventory> getCapabilityHandler(Direction side) {
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

    public static class InternalWrapper extends ItemStackHandler{

        private ItemInventory owner;
        private boolean input;

        public InternalWrapper(int slots, boolean input) {
            super(slots);
            this.input = input;
        }

        public void setOwner(ItemInventory owner) {
            this.owner = owner;
        }

        public void setItems(NonNullList<ItemStack> stacks) {
            this.stacks = stacks;
        }

        public NonNullList<ItemStack> getStacks() {
            return stacks;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        public @NotNull ItemStack internalInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        public @NotNull ItemStack internalExtractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return input ? super.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return input ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }

        public boolean isInput() {
            return input;
        }

    }
}
