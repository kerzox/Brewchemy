package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.recipes.FermentationRecipe;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class WoodenBarrelBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final ItemStackInventory inventory = new ItemStackInventory(1, 1);
    private final FluidStorageTank fluidTank = new FluidStorageTank(1);
    private final LazyOptional<FluidStorageTank> handler = LazyOptional.of(() -> fluidTank);

    private final HashMap<FluidStack, FermentationRecipe> cachedRecipe = new HashMap<>();

    private boolean running;
    private long tick;

    public WoodenBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.WOODEN_BARREL.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {
        tick++;
        if (fluidTank.isEmpty()) return;
        FluidStack fluid = fluidTank.getFluid();
        if (cachedRecipe.get(fluid) != null) {
            doRecipe(cachedRecipe.get(fluid));
        } else { // fluid changed
            cachedRecipe.clear();
            Optional<FermentationRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), new RecipeInventoryWrapper(fluidTank, inventory), level);
            recipe.ifPresent(r -> cachedRecipe.put(fluid, r));
        }
    }

    private void doRecipe(FermentationRecipe fermentationRecipe) {
        FluidStack result = fermentationRecipe.getResultFluid();
        if (result.isEmpty()) return;

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventory.getHandler(side);
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }
}
