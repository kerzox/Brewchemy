package mod.kerzox.brewchemy.common.blockentity.base;

import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class RecipeBlockEntity<T extends AbstractRecipe<RecipeInventory>> extends CapabilityBlockEntity implements IServerTickable {

    protected RecipeInventory recipeInventory;
    protected RecipeType<T> recipeType;
    protected Optional<T> currentRecipe = Optional.empty();

    protected boolean working;
    protected int recipeDuration;
    protected int maxRecipeDuration;

    protected int tick;
    protected int pTick;

    public RecipeBlockEntity(BlockEntityType<?> type, RecipeType<T> recipeType, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.recipeType = recipeType;
    }

    public abstract RecipeInventory getRecipeInventory();

    public void startRecipe(T recipeToWork) {
        this.currentRecipe = Optional.of(recipeToWork);
        this.maxRecipeDuration = recipeToWork.getDuration();
        this.recipeDuration = recipeToWork.getDuration();
        this.working = true;
    }

    protected void finishRecipe() {
        this.working = false;
        this.currentRecipe = Optional.empty();
        this.recipeDuration = 0;
        syncBlockEntity();
    }

    protected boolean checkRecipe() {
        if (this.currentRecipe.isPresent()) {
            if (findValidRecipe().isEmpty()) {
                finishRecipe();
                return false;
            } else {
                return currentRecipe.get().equals(findValidRecipe().get());
            }
        } else return false;
    }

    protected Optional<T> findValidRecipe() {
        return level.getRecipeManager().getRecipeFor(recipeType, getRecipeInventory(), level);
    }

    protected int getAmountToProgress() {
        return 1;
    }

    protected abstract boolean hasAResult(T workingRecipe);

    protected abstract void onRecipeFinish(T workingRecipe);

    @Override
    public void tick() {
        if (level != null) {
            if (currentRecipe.isEmpty()) {
                findValidRecipe().ifPresent(this::doRecipe);
            }
            else doRecipe(currentRecipe.get());
        }
        tick = (tick + 1) % 1_728_000;
        pTick = tick;
    }

    protected void doRecipe(T workingRecipe) {

        // check for a result if we don't we skip this
        if (!hasAResult(workingRecipe)) {
            finishRecipe();
            return;
        }

        // getting this far we must have a valid recipe and a valid result (either fluid or item)

        if (!isWorking()) {
            startRecipe(workingRecipe);
        }

        if (isWorking()) {

            if (getRecipeDuration() <= 0) {
                if (checkRecipe()) onRecipeFinish(workingRecipe);
            }

            if (!canProgress(workingRecipe)) {
                return;
            }

            recipeDuration -= getAmountToProgress();

            syncBlockEntity();

        }

    }

    /**
     * canProgress
     * A conditional just before the duration is lowered can check for enough energy to continue etc.
     * @return whether the recipe can progress further
     */

    protected abstract boolean canProgress(T workingRecipe);

    public boolean isWorking() {
        return working;
    }

    public int getMaxRecipeDuration() {
        return maxRecipeDuration;
    }

    public int getRecipeDuration() {
        return recipeDuration;
    }

    public static List<Integer> hasEnoughFluidSlots(FluidStack[] fResult, IFluidHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (FluidStack resultStack : fResult) {
            for (int index = 0; index < handler.getTanks(); index++) {
                if (handler instanceof MultifluidInventory.InternalWrapper wrapper) {;
                    int filledAmount = wrapper.internalFill(resultStack, IFluidHandler.FluidAction.SIMULATE);
                    if (!slotsUsed.contains(index) && filledAmount == resultStack.getAmount()) {
                        slotsUsed.add(index);
                        break;
                    }
                } else {
                    int filledAmount = handler.fill(resultStack, IFluidHandler.FluidAction.SIMULATE);
                    if (!slotsUsed.contains(index) && filledAmount == resultStack.getAmount()) {
                        slotsUsed.add(index);
                        break;
                    }
                }
            }
        }
        return slotsUsed;
    }

    public static void useFluidIngredients(NonNullList<FluidIngredient> fluidIngredients, IFluidHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (FluidIngredient ingredient : fluidIngredients) {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack tank = handler.getFluidInTank(i);
                if (ingredient.testFluidWithAmount(tank)) {
                    slotsUsed.add(i);
                    tank.shrink(ingredient.getAmount());
                    break;
                }
            }
        }
    }

    public static void useIngredients(NonNullList<Ingredient> specificIngredients, IItemHandler handler, int amountToShrink) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (Ingredient ingredient : specificIngredients) {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!slotsUsed.contains(i) && ingredient.test(handler.getStackInSlot(i))) {
                    slotsUsed.add(i);
                    handler.getStackInSlot(i).shrink(amountToShrink);
                    if (handler instanceof ItemInventory.InternalWrapper inventory) {
                        inventory.getOwner().onContentsChanged(i, true);
                    }
                    break;
                }
            }
        }
    }

    public static void useSizeSpecificIngredients(NonNullList<Ingredient> specificIngredients, IItemHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (Ingredient ingredient : specificIngredients) {
            if (ingredient instanceof SizeSpecificIngredient ingredient1) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    if (!slotsUsed.contains(i) && ingredient.test(handler.getStackInSlot(i))) {
                        slotsUsed.add(i);
                        handler.getStackInSlot(i).shrink(ingredient1.getSize());
                        if (handler instanceof ItemInventory.InternalWrapper inventory) {
                            inventory.getOwner().onContentsChanged(i, true);
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void useSizeSpecificIngredients(NonNullList<SizeSpecificIngredient> specificIngredients, IItemHandler handler, int slots) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (SizeSpecificIngredient ingredient : specificIngredients) {
            for (int i = 0; i < slots; i++) {
                if (!slotsUsed.contains(i) && ingredient.test(handler.getStackInSlot(i))) {
                    slotsUsed.add(i);
                    handler.getStackInSlot(i).shrink(ingredient.getSize());
                    break;
                }
            }
        }
    }

    public static List<Integer> hasEnoughItemSlots(ItemStack[] result, IItemHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();

        for (ItemStack resultItemStack : result) {
            for (int index = 0; index < handler.getSlots(); index++) {
                ItemStack ret = handler.insertItem(index, resultItemStack, true);
                if (handler instanceof ItemInventory.InternalWrapper internal) {
                    if (!internal.isInput()) ret = internal.internalInsertItem(index, resultItemStack, true);
                }
                if (!slotsUsed.contains(index) && ret.isEmpty()) {
                    slotsUsed.add(index);
                    break;
                }
            }
        }
        return slotsUsed;
    }

    public static void transferFluidResults(FluidStack[] result, IFluidHandler handler) {
        for (FluidStack resultFluidStack : result) {
            if (handler instanceof MultifluidInventory.InternalWrapper wrapper) {
                wrapper.internalFill(resultFluidStack, IFluidHandler.FluidAction.EXECUTE);
                continue;
            }
            handler.fill(resultFluidStack, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public static void transferItemResults(ItemStack[] result, IItemHandler handler) {
        for (ItemStack resultItemStack : result) {
            for (int index = 0; index < handler.getSlots(); index++) {
                if (handler instanceof ItemInventory.InternalWrapper internal) {
                    if (!internal.isInput()) {
                        if (internal.internalInsertItem(index, resultItemStack, true).isEmpty()) {
                            internal.internalInsertItem(index, resultItemStack, false);
                            break;
                        }
                    }
                } else {
                    if (handler.insertItem(index, resultItemStack, true).isEmpty()) {
                        handler.insertItem(index, resultItemStack, false);
                        break;
                    }
                }
            }
        }
    }


}


