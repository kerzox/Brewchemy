package mod.kerzox.brewchemy.common.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class AbstractItemRecipe<C extends Container> extends AbstractRecipe<C> {

    protected final ItemStack[] results;

    public AbstractItemRecipe(RecipeType<?> type, ResourceLocation id, String group, int duration, ItemStack[] results, RecipeSerializer<?> serializer) {
        super(type, id, group, duration, serializer);
        this.results = results;
    }

    public AbstractItemRecipe(RecipeType<?> type, ResourceLocation id, String group, int duration, ItemStack result, RecipeSerializer<?> serializer) {
        super(type, id, group, duration, serializer);
        this.results = new ItemStack[] { result };
    }

    public ItemStack[] assembleResultItems(RecipeInventory inv, RegistryAccess access) {
        return results.clone();
    }

    public ItemStack[] getRecipeResults(RegistryAccess p_267052_) {
        return results;
    }

    // shouldn't use these in recipe code
    @Override
    public ItemStack assemble(RecipeInventory inv, RegistryAccess access) {
        return results[0].copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return results[0];
    }

}
