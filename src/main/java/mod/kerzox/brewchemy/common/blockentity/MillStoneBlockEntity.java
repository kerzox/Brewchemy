package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.recipes.MillstoneRecipe;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MillStoneBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final ItemStackInventory inventory = new ItemStackInventory(1, 1);
    private final LazyOptional<ItemStackInventory> lazyOptionalOfInventory = LazyOptional.of(() -> inventory);

    private int recipeDuration;
    private final int revolutions = 5; // how many times a crank will rotate before recipe finishes.
    private boolean running;

    public MillStoneBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.MILL_STONE.get(), pWorldPosition, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyOptionalOfInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onServer() {
        checkForRecipes();
    }

    private void checkForRecipes() {
        RecipeInventoryWrapper recipeInv = null;
        // check for items
        for (int i = 0; i < inventory.getInputHandler().getSlots(); i++) {
            if (!inventory.getInputHandler().getStackInSlot(i).isEmpty()) {
                recipeInv = new RecipeInventoryWrapper(this.inventory.getInputHandler());
            }
        }
        // if no items exit
        if (recipeInv == null) return;
        // check if recipe can be found from items
        Optional<MillstoneRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.MILLSTONE_RECIPE.get(), recipeInv, level);
        if (recipe.isPresent()) {
            doRecipe(recipe.get(), recipeInv);
        }
    }

    private void doRecipe(MillstoneRecipe recipe, RecipeInventoryWrapper recipeInventoryWrapper) {
        ItemStack result = recipe.assemble(recipeInventoryWrapper);
        
        if (!result.isEmpty()) {
            if (!running) {
                recipeDuration = recipe.getDuration();
                running = true;
            }

        }

    }

}
