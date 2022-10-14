package mod.kerzox.brewchemy.common.crafting;

import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class RecipeInventoryWrapper extends RecipeWrapper {

    // we can't make this look nicer i don't think so meh

    protected IFluidHandler fluidInventory;
    private boolean sideAgnostic;

    public RecipeInventoryWrapper(IItemHandlerModifiable inv) {
        super(inv);
    }

    public RecipeInventoryWrapper(IFluidHandler fluid, IItemHandlerModifiable item, boolean sideAgnostic) {
        super(item);
        this.fluidInventory = fluid;
        this.sideAgnostic = sideAgnostic;
    }

    public RecipeInventoryWrapper(IFluidHandler inv) {
        super(new ItemStackHandler());
        this.fluidInventory = inv;
    }

    public boolean sideAgnostic() {
        return sideAgnostic;
    }

    public boolean canStorageFluid() {
        return fluidInventory != null;
    }

    public IFluidHandler getFluidInventory() {
        return fluidInventory;
    }


}
