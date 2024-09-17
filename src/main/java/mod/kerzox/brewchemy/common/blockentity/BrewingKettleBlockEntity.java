package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.RecipeBlockEntity;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.recipe.BrewingRecipe;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BrewingKettleBlockEntity extends RecipeBlockEntity<BrewingRecipe> {

    public BrewingKettleBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.BREWING_KETTLE_BLOCK_ENTITY.get(), BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pos, state);
    }


    @Override
    public RecipeInventory getRecipeInventory() {
        return null;
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
