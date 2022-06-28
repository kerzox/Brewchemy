package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultitankFluid;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedFluidTank;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedMultifluidTank;
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

public class BoilKettleBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final ItemStackHandler inventory = new ItemStackHandler(1);
    private final SidedMultifluidTank sidedFluidTank = new SidedMultifluidTank(2, PintGlassItem.KEG_VOLUME, 2, PintGlassItem.KEG_VOLUME);
    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> sidedFluidTank);
    private final LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(() -> inventory);
    private boolean running;
    private int duration;

    public BoilKettleBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.BREWING_POT.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {


//        Optional<BrewingRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), new RecipeInventoryWrapper(fluidTank, inventory), level);
//        recipe.ifPresent(this::doRecipe);
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

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer) {
        if (!pLevel.isClientSide) {
            if (pPlayer.getMainHandItem().getItem() == Items.DIAMOND) {
                this.sidedFluidTank.drain(new FluidStack(BrewchemyRegistry.Fluids.BEER.getFluid().get(), 500), IFluidHandler.FluidAction.EXECUTE);
            }
            else if (pPlayer.getMainHandItem().getItem() == Items.BLAZE_ROD) {
                this.sidedFluidTank.getOutputHandler().forceFill(this.sidedFluidTank.drain(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                this.sidedFluidTank.getOutputHandler().forceFill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
            }
            else if (pPlayer.getMainHandItem().getItem() == Items.STICK) {
                this.sidedFluidTank.fill(new FluidStack(BrewchemyRegistry.Fluids.BEER.getFluid().get(), 500), IFluidHandler.FluidAction.EXECUTE);
            } else {
                for (int i = 0; i < this.sidedFluidTank.getTanks(); i++) {
                    pPlayer.sendSystemMessage(Component.literal(this.sidedFluidTank.getFluidInTank(i).getFluid() + " : " + this.sidedFluidTank.getFluidInTank(i).getAmount()));
                }
            }
        }
        return super.onPlayerClick(pLevel, pPlayer);
    }

    private void doRecipe(BrewingRecipe recipe) {
        FluidStack result = recipe.getResultFluid();
        if (result.isEmpty()) return;
        if (!running) {
            duration = recipe.getDuration();
            running = true;
        }
//        if (duration <= 0) {
//            if (this.fluidTank.fill(result, IFluidHandler.FluidAction.SIMULATE) != 0) {
//                for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
//                    for (FluidStack stack : ingredient.getStacks()) {
//                        this.fluidTank.drain(stack, IFluidHandler.FluidAction.EXECUTE);
//                        this.inventory.getStackInSlot(0).shrink(1);
//                    }
//                }
//                running = false;
//            }
//        }
        duration--;
    }
}
