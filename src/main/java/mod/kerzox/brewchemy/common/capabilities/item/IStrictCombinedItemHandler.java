package mod.kerzox.brewchemy.common.capabilities.item;

import mod.kerzox.brewchemy.common.capabilities.IStrictInventory;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IStrictCombinedItemHandler<T> extends IItemHandlerModifiable, IStrictInventory<T> {

    IItemHandlerModifiable getInputHandler();
    IItemHandlerModifiable getOutputHandler();

}
