package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.RecipeBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.SingleFluidInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.recipe.CultureJarRecipe;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CultureJarBlockEntity extends RecipeBlockEntity<CultureJarRecipe> {

    private final SingleFluidInventory fluidHandler = (SingleFluidInventory) SingleFluidInventory.simple(1000).addInput(Direction.values());
    private final ItemInventory itemHandler = ItemInventory.of(1, 1);

    public CultureJarBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.CULTURE_JAR_BLOCK_ENTITY.get(), BrewchemyRegistry.Recipes.CULTURE_JAR_RECIPE.get(), pos, state);
        addCapabilities(itemHandler, fluidHandler);
    }

    @Override
    public RecipeInventory getRecipeInventory() {
        return new RecipeInventory(itemHandler, fluidHandler);
    }

    @Override
    protected boolean hasAResult(CultureJarRecipe workingRecipe) {
        return false;
    }

    @Override
    protected void onRecipeFinish(CultureJarRecipe workingRecipe) {

    }


    @Override
    protected boolean canProgress(CultureJarRecipe workingRecipe) {
        return false;
    }
}
