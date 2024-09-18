package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.RecipeBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidInventory;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.recipe.BrewingRecipe;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BrewingKettleBlockEntity extends RecipeBlockEntity<BrewingRecipe> {

    private final MultifluidInventory fluidHandler = MultifluidInventory.of(MultifluidTank.of(2, 16000), MultifluidTank.of(1, 32000));
    private final ItemInventory itemHandler = ItemInventory.of(2, 1);

    public BrewingKettleBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.BREWING_KETTLE_BLOCK_ENTITY.get(), BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pos, state);
        addCapabilities(itemHandler, fluidHandler);
    }

    @Override
    public RecipeInventory getRecipeInventory() {
        return new RecipeInventory(itemHandler, fluidHandler);
    }

    @Override
    protected boolean hasAResult(BrewingRecipe workingRecipe) {
        return false;
    }

    @Override
    protected void onRecipeFinish(BrewingRecipe workingRecipe) {

    }

    @Override
    protected boolean canProgress() {
        return false;
    }
}
