package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.RecipeBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.SingleFluidInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.recipe.CultureJarRecipe;
import mod.kerzox.brewchemy.common.data.BrewingKettleHeating;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class CultureJarBlockEntity extends RecipeBlockEntity<CultureJarRecipe> {

    private final SingleFluidInventory fluidHandler = (SingleFluidInventory) SingleFluidInventory.simple(1000).addInput(Direction.values());
    private final ItemInventory itemHandler = ItemInventory.of(1, 1);

    public CultureJarBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.CULTURE_JAR_BLOCK_ENTITY.get(), BrewchemyRegistry.Recipes.CULTURE_JAR_RECIPE.get(), pos, state);
        addCapabilities(itemHandler, fluidHandler);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {

        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            ItemHandlerHelper.giveItemToPlayer(pPlayer, itemHandler.getOutputHandler().internalExtractItem(0, 1, false));
        }

        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public RecipeInventory getRecipeInventory() {
        return new RecipeInventory(itemHandler, fluidHandler);
    }

    @Override
    protected boolean hasAResult(CultureJarRecipe workingRecipe) {
        return !workingRecipe.assemble(getRecipeInventory(), RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(CultureJarRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(getRecipeInventory(), RegistryAccess.EMPTY);

        IFluidHandler temp = fluidHandler.getInputWrapper().get().copy();
        useFluidIngredients(workingRecipe.getFluidIngredients(), temp);

        if (hasEnoughItemSlots(new ItemStack[]{result}, itemHandler.getOutputHandler()).size() != 1) return;
        transferItemResults(new ItemStack[]{result}, itemHandler.getOutputHandler());

        useFluidIngredients(workingRecipe.getFluidIngredients(), fluidHandler.getInputWrapper());

        finishRecipe();

    }


    @Override
    protected boolean canProgress(CultureJarRecipe workingRecipe) {
        int maxHeatFromSource = BrewingKettleHeating.getHeat(level.getBlockState(worldPosition.below()).getBlock());
        return workingRecipe.getHeat() < 0 ? workingRecipe.getHeat() >= maxHeatFromSource : workingRecipe.getHeat() <= maxHeatFromSource;
    }
}
