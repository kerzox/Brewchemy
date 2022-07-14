package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultitankFluid;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedFluidTank;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedMultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.BrewingRecipe;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BoilKettleBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final ItemStackInventory.InputHandler inventory = new ItemStackInventory.InputHandler(1);
    private final SidedMultifluidTank sidedFluidTank = new SidedMultifluidTank(1, PintGlassItem.KEG_VOLUME, 1, PintGlassItem.KEG_VOLUME);
    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> sidedFluidTank);
    private final LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(() -> inventory);
    private boolean running = false;
    private int duration;

    public BoilKettleBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.BREWING_POT.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {
        Optional<BrewingRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), new RecipeInventoryWrapper(sidedFluidTank, inventory), level);
        recipe.ifPresent(this::doRecipe);
    }

    private boolean hasHeatForRecipe(BrewingRecipe recipe) {
        if (recipe.getHeat() == BrewingRecipe.FIRE) {
            return level.getBlockState(this.getBlockPos().below()).getBlock() instanceof BaseFireBlock;
        } else if (recipe.getHeat() == BrewingRecipe.SUPERHEATED) {
            return level.getBlockState(this.getBlockPos().below()).getBlock() instanceof SoulFireBlock;
        } else {
            return true;
        }
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

    private void doRecipe(BrewingRecipe recipe) {
        FluidStack result = recipe.assembleFluid(new RecipeInventoryWrapper(sidedFluidTank, inventory));
        if (result.isEmpty() || hasHeatForRecipe(recipe)) return;
        if (!running) {
            duration = recipe.getDuration();
            running = true;
        }
        if (duration <= 0) {
            if (this.sidedFluidTank.getOutputHandler().forceFill(result, IFluidHandler.FluidAction.SIMULATE) != 0) {
                for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
                    for (FluidStack stack : ingredient.getStacks()) {
                        this.sidedFluidTank.drain(stack, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
                this.inventory.getStackInSlot(0).shrink(1);
                this.sidedFluidTank.getOutputHandler().forceFill(result, IFluidHandler.FluidAction.EXECUTE);
                running = false;
            }
        }
        duration--;
    }
}
