package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.block.BoilKettleBlock;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedMultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.CountSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.BrewingRecipe;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BoilKettleBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final ItemStackInventory.InputHandler inventory = new ItemStackInventory.InputHandler(1);
    private final SidedMultifluidTank sidedFluidTank = new SidedMultifluidTank(1, PintGlassItem.KEG_VOLUME, 1, PintGlassItem.KEG_VOLUME);
    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> sidedFluidTank);
    private final LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(() -> inventory);
    private boolean running = false;
    private int duration;
    private int heat;
    private int currentRecipeHeat;
    private int tick;
    private boolean stateChanged = false;

    public BoilKettleBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.BREWING_POT.get(), pWorldPosition, pBlockState);
    }

    private int getHeatSource() {
        if (level.getBlockState(this.getBlockPos().below()).getBlock() instanceof BaseFireBlock) {
            return level.getBlockState(this.getBlockPos().below()).getBlock() instanceof SoulFireBlock ? BrewingRecipe.SUPERHEATED : BrewingRecipe.FIRE;
        }
        return BrewingRecipe.NO_HEAT;
    }

    @Override
    public void onServer() {
        tick++;
        calculateHeat();
        Optional<BrewingRecipe> recipe = this.findValidRecipe(new RecipeInventoryWrapper(sidedFluidTank, inventory));
        recipe.ifPresent(this::doRecipe);
        syncBlockEntity();
        System.out.println("Input Tank: " + this.sidedFluidTank.getFluidInTank(0).getAmount() + "\nOutput Tank: " + this.sidedFluidTank.getFluidInTank(1).getAmount() + "\nItems: " + this.inventory.getStackInSlot(0));
    }

    private void calculateHeat() {
        if (heat < getHeatSource()) {
            heat++;
        } else {
            if (heat > 0) {
                if (tick % 20 == 0) { // loses heat every second
                    heat--;
                }
            }
        }
    }

    public Optional<BrewingRecipe> findValidRecipe(RecipeInventoryWrapper wrapper) {
        return level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), wrapper, level);
    }

    public void doRecipe(BrewingRecipe recipe) {
        FluidStack result = recipe.assembleFluid(new RecipeInventoryWrapper(sidedFluidTank, inventory));
        if (result.isEmpty()) return;
        if (!hasHeatForRecipe(recipe)) return;
        if (!running) {
            duration = recipe.getDuration();
            running = true;
        }

        if (duration <= 0) {
            if (this.sidedFluidTank.getOutputHandler().getStorageTank(0).isFull()) return;
            for (CountSpecificIngredient cIngredient : recipe.getCIngredients()) {
                for (ItemStack item : cIngredient.getItems()) {
                    if (inventory.getStackInSlot(0).is(item.getItem())) {
                        inventory.getStackInSlot(0).shrink(item.getCount());
                        break;
                    }
                }
            }
            for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
                for (FluidStack fluid : fluidIngredient.getStacks()) {
                    if (sidedFluidTank.getInputHandler().getFluidInTank(0).getFluid().isSame(fluid.getFluid())) {
                        sidedFluidTank.getInputHandler().getFluidInTank(0).shrink(fluid.getAmount());
                        break;
                    }
                }
            }
            sidedFluidTank.getOutputHandler().forceFill(result, IFluidHandler.FluidAction.EXECUTE);
            running = false;
        }
        duration--;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            if (getBlockState().getBlock() instanceof BoilKettleBlock kettle) {
                BlockPos pos = getBlockPos();
                if (!kettle.isOpened(getBlockState())) {
                    level.setBlockAndUpdate(getBlockPos(), kettle.openLid(getBlockState()));

                } else {
                    level.setBlockAndUpdate(getBlockPos(), kettle.closeLid(getBlockState()));
                }
            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    private boolean hasHeatForRecipe(BrewingRecipe recipe) {
        return recipe.getHeat() <= heat;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("duration", this.duration);
        pTag.putInt("heat", this.heat);
        pTag.putInt("recipeHeat", this.currentRecipeHeat);
        this.sidedFluidTank.serializeNBT();
        this.inventory.serializeNBT();
    }

    @Override
    protected void read(CompoundTag pTag) {
       this.duration = pTag.getInt("duration");
       this.heat = pTag.getInt("heat");
       this.currentRecipeHeat = pTag.getInt("recipeHeat");
       this.sidedFluidTank.deserializeNBT(pTag);
       this.inventory.deserializeNBT(pTag);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return this.sidedFluidTank.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

//    private void doRecipe(BrewingRecipe recipe) {

//    }

    public int getDuration() {
        return duration;
    }

    public int getCurrentRecipeHeat() {
        return currentRecipeHeat;
    }

    public ItemStackInventory.InputHandler getInventory() {
        return inventory;
    }

    public SidedMultifluidTank getSidedFluidTank() {
        return sidedFluidTank;
    }

    public boolean hasStateChanged() {
        return this.stateChanged;
    }

    public int getHeat() {
        return heat;
    }

    public void onTopRemoved() {

    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }


}
