package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import mod.kerzox.brewchemy.common.capabilities.CapabilityUtils;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.FermentJarRecipe;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FermentsJarBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final int OUTPUT_SLOT = 0;
    private final ItemStackInventory.OutputHandler inventory = new ItemStackInventory.OutputHandler(1);
    private final LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(() -> inventory);

    private final FluidStorageTank tank = new FluidStorageTank(1000);
    private final LazyOptional<FluidStorageTank> fluidHandler = LazyOptional.of(() -> tank);

    private boolean running;
    private int duration;

    private FluidStack prevFluid = FluidStack.EMPTY;

    public FermentsJarBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.FERMENTS_JAR.get(), pWorldPosition, pBlockState);
    }

    public FluidStorageTank getTank() {
        return tank;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("duration", this.duration);
        pTag.put("fluidHandler", this.tank.writeToNBT(new CompoundTag()));
        pTag.put("itemHandler", this.inventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.duration = pTag.getInt("duration");
        this.tank.readFromNBT(pTag.getCompound("fluidHandler"));
        this.inventory.deserializeNBT(pTag.getCompound("itemHandler"));
    }

    @Override
    public void onServer() {
        if (this.tank.isEmpty()) return;
        if (!this.tank.getFluid().isFluidStackIdentical(prevFluid)) {
            prevFluid = this.tank.getFluid();
            syncBlockEntity();
        }
        Optional<FermentJarRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.FERMENTS_JAR_RECIPE.get(), new RecipeInventoryWrapper(tank), level);
        recipe.ifPresent(this::doRecipe);

    }

    private void doRecipe(FermentJarRecipe r) {
        ItemStack result = r.assemble(new RecipeInventoryWrapper(tank));
        if (result.isEmpty()) return;
        if (!running) {
            duration = r.getDuration();
            running = true;
        }
        if (duration <= 0) {
            if (this.inventory.isSlotFull(OUTPUT_SLOT)) return;
            this.inventory.forceInsertItem(OUTPUT_SLOT, result, false);
            FluidStack fluidStack = this.tank.getFluid();
            for (FluidIngredient fluidIngredient : r.getFluidIngredients()) {
                if (fluidIngredient.test(fluidStack)) {
                    fluidStack.shrink(fluidIngredient.getAmountFromIngredient(fluidStack));
                }
            }
            running = false;
        }
        syncBlockEntity();
        duration--;
     }

    @Override
    public boolean onPlayerClick(Level level, Player player, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!level.isClientSide) {
            if (!this.inventory.getStackInSlot(0).isEmpty()) {
                CapabilityUtils.tryPlayerInventoryInsert(this.inventory, OUTPUT_SLOT, player);
            }
        }
        return false;
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

}
