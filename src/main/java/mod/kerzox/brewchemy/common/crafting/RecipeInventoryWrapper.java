package mod.kerzox.brewchemy.common.crafting;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class RecipeInventoryWrapper extends RecipeWrapper {

    // just another wrapper in case we want to do fun stuff

    public RecipeInventoryWrapper(IItemHandlerModifiable inv) {
        super(inv);
    }
}
