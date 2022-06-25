package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.recipes.FermentJarRecipe;
import mod.kerzox.brewchemy.common.crafting.recipes.MillstoneRecipe;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FermentsJarBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final ItemStackHandler inventory = new ItemStackHandler(1);
    private final LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(() -> inventory);

    private final FluidStorageTank tank = new FluidStorageTank(1000);
    private final LazyOptional<FluidStorageTank> fluidHandler = LazyOptional.of(() -> tank);

    private boolean running;
    private int duration;

    public FermentsJarBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.FERMENTS_JAR.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {
        if (this.tank.isEmpty()) return;
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
            if (this.inventory.insertItem(0, result, true).isEmpty()) {
                this.tank.drain(r.getFluidIngredients().get(0).getStacks()[0].copy(), IFluidHandler.FluidAction.EXECUTE);
                this.inventory.insertItem(0, result, false);
                running = false;
            }
        }
        duration--;
     }

    @Override
    public boolean onPlayerClick(Level level, Player player) {
        if (!level.isClientSide) {
            player.sendSystemMessage(Component.literal(String.valueOf(tank.getFluidAmount())));
            player.sendSystemMessage(Component.literal(String.valueOf(inventory.getStackInSlot(0))));
        }
        return false;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }
        else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
