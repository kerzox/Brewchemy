package mod.kerzox.brewchemy.common.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractFluidRecipe<C extends Container> extends AbstractRecipe<C> {

    protected final FluidStack[] results;

    public AbstractFluidRecipe(RecipeType<?> type, ResourceLocation id, String group, int duration, FluidStack[] results, RecipeSerializer<?> serializer) {
        super(type, id, group, duration, serializer);
        this.results = results;
    }

    public AbstractFluidRecipe(RecipeType<?> type, ResourceLocation id, String group, int duration, FluidStack results, RecipeSerializer<?> serializer) {
        super(type, id, group, duration, serializer);
        this.results = new FluidStack[] { results};
    }

    public FluidStack[] assembleResultItems(RecipeInventory inv, RegistryAccess access) {
        return results.clone();
    }

    public FluidStack[] getRecipeResults(RegistryAccess p_267052_) {
        return results;
    }

    // shouldn't use these in recipe code
    @Override
    public ItemStack assemble(RecipeInventory inv, RegistryAccess access) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return ItemStack.EMPTY;
    }

}
