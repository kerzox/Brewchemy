package mod.kerzox.brewchemy.common.capabilities.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

public class ItemStackInventory extends CombinedInvWrapper {

    public ItemStackInventory(int inputSlots, int outputSlots) {
        super(new InputHandler(inputSlots), new OutputHandler(outputSlots));
    }

    public ItemStackInventory(InputHandler inputHandler, OutputHandler outputHandler) {
        super(inputHandler, outputHandler);
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
        System.out.println(index);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }

    public static class InputHandler extends ItemStackHandler {

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
    }

    public static class OutputHandler extends ItemStackHandler {

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
    }

}
