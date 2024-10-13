package mod.kerzox.brewchemy.common.crafting;

import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidInventory;
import mod.kerzox.brewchemy.common.capabilities.fluid.SingleFluidInventory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public class RecipeInventory extends RecipeWrapper {

    protected IRecipeExtraValidator validator = new IRecipeExtraValidator() {
        @Override
        public boolean isValid(Recipe<?> recipe) {
            return true;
        }
    };
    protected IFluidHandler fluidInv;

    public RecipeInventory(IItemHandlerModifiable inv, IFluidHandler fluidInv) {
        super(inv);
        this.fluidInv = fluidInv;
    }

    public RecipeInventory(IItemHandlerModifiable inv, IFluidHandler fluidInv, IRecipeExtraValidator validator) {
        super(inv);
        this.fluidInv = fluidInv;
        this.validator = validator;
    }

    public RecipeInventory(IItemHandlerModifiable inv) {
        super(inv);
    }

    public RecipeInventory(IFluidHandler fluidInv) {
        super(new ItemStackHandler(1));
        this.fluidInv = fluidInv;
    }

    public Optional<MultifluidInventory> getAsMultifluidInventory() {
        if (fluidInv instanceof MultifluidInventory fluidInventory) return Optional.of(fluidInventory);
        else return Optional.empty();
    }

    public Optional<SingleFluidInventory> getAsSingleFluidInventory() {
        if (fluidInv instanceof SingleFluidInventory fluidInventory) return Optional.of(fluidInventory);
        else return Optional.empty();
    }

    public boolean canStorageFluid() {
        return this.fluidInv != null;
    }

    public IFluidHandler getFluidHandler() {
        return fluidInv;
    }

    public IRecipeExtraValidator getValidator() {
        return validator;
    }

    public interface IRecipeExtraValidator {
        boolean isValid(Recipe<?> recipe);
    }

}
