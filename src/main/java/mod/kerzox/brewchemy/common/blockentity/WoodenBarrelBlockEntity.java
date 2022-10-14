package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.menu.FermentationBarrelMenu;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedMultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.recipes.FermentationRecipe;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.common.util.FermentationHelper;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WoodenBarrelBlockEntity extends BrewchemyBlockEntity implements IServerTickable, MenuProvider {

    private final ItemStackInventory inventory = new ItemStackInventory(2, 1);
    private final SidedMultifluidTank fluidTank = new SidedMultifluidTank(1, PintGlassItem.KEG_VOLUME, 1, PintGlassItem.KEG_VOLUME);
    private final FluidStack[] inputStacks = new FluidStack[]{FluidStack.EMPTY};

    private boolean running;
    private int fermentationTicks;
    private int tick;
    private float catalystAmount;
    private float cost;
    private float maxCost;

    private boolean blockFermentation;

    private RecipeInventoryWrapper recipeInventoryWrapper;
    private FluidStack fermentingStack = FluidStack.EMPTY;

    public WoodenBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.WOODEN_BARREL.get(), pWorldPosition, pBlockState);
    }

    public SidedMultifluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public void onServer() {
        ItemStack item = this.inventory.getStackFromInputHandler(1);
        if (!item.isEmpty()) {
            FluidActionResult result = FluidUtil.tryEmptyContainer(item, this.fluidTank.getInputHandler().getStorageTank(0), 1000, null, false);
            if (result.success) {
                if (this.inventory.getOutputHandler().forceInsertItem(0, result.getResult(), true).isEmpty()) {
                    FluidActionResult doEmpty = FluidUtil.tryEmptyContainer(item, this.fluidTank.getInputHandler().getStorageTank(0), 1000, null, true);
                    item.shrink(1);
                    this.inventory.getOutputHandler().forceInsertItem(0, doEmpty.getResult(), false);
                }
                return;
            }
            FluidActionResult result1 = FluidUtil.tryFillContainer(item, this.fluidTank.getOutputHandler().getStorageTank(0), 1000, null, false);
            if (result1.success) {
                if (this.inventory.getOutputHandler().forceInsertItem(0, result1.getResult(), true).isEmpty()) {
                    FluidActionResult doFillFromOutput = FluidUtil.tryFillContainer(item, this.fluidTank.getOutputHandler().getStorageTank(0), 1000, null, true);
                    item.shrink(1);
                    this.inventory.getOutputHandler().forceInsertItem(0, doFillFromOutput.getResult(), false);
                }
                return;
            }
            if (!running) {
                FluidActionResult result2 = FluidUtil.tryFillContainer(item, this.fluidTank.getInputHandler().getStorageTank(0), 1000, null, false);
                if (result2.success) {
                    if (this.inventory.getOutputHandler().forceInsertItem(0, result2.getResult(), true).isEmpty()) {
                        FluidActionResult doFillFromInput = FluidUtil.tryFillContainer(item, this.fluidTank.getInputHandler().getStorageTank(0), 1000, null, true);
                        item.shrink(1);
                        this.inventory.getOutputHandler().forceInsertItem(0, doFillFromInput.getResult(), false);
                    }
                    return;
                }
            }
        }

        if (!blockFermentation) {
            if (recipeInventoryWrapper == null) {
                ItemStackHandler stackHandler = new ItemStackHandler(1);
                stackHandler.setStackInSlot(0, this.inventory.getStackFromInputHandler(0).copy());
                recipeInventoryWrapper = new RecipeInventoryWrapper(this.fluidTank, stackHandler, false);
            }
            Optional<FermentationRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), recipeInventoryWrapper, level);
            recipe.ifPresent(this::doRecipe);
            if (!recipe.isPresent()) recipeInventoryWrapper = null;
        }

    }

    public boolean isFermentationBlocked() {
        return blockFermentation;
    }

    public int getTick() {
        return tick;
    }

    private void doRecipe(FermentationRecipe recipe) {
        FluidStack result = recipe.assembleFluid(new RecipeInventoryWrapper(this.fluidTank, inventory, false));
        FluidStack input = this.fluidTank.getFluidInTank(0);
        ItemStack catalyst = inventory.getStackFromInputHandler(0);
        int recipeAmount = recipe.getCatalystIngredient().getSize();
        float amountNeeded = (((float) recipeAmount * input.getAmount()) / recipe.getFluidIngredient().getProxy().getAmount());
        if (result.isEmpty()) return;

        if (!running) {
//            float prevCost = 0;
//            if (cost != 0) prevCost = Math.round((FermentationHelper.Stages.MATURE.getTime() / catalystAmount)) - cost;
            catalystAmount = amountNeeded;
            cost = Math.round((FermentationHelper.Stages.MATURE.getTime() / amountNeeded));
            fermentationTicks = 0;
            if (!catalyst.isEmpty()) catalyst.shrink(1);
            running = true;
        } else {
            if (cost <= 0) {
                if (catalyst.isEmpty()) {
                    return;
                }
                if (catalystAmount > 0) {
                    catalyst.shrink(1);
                    cost = FermentationHelper.Stages.MATURE.getTime() / amountNeeded;
                }
                catalystAmount--;
            }
            cost--;
            fermentationTicks++;
            writeNBTtoFluidStack(input);
            if (FermentationHelper.getFermentationStage(input) == FermentationHelper.Stages.MATURE) {
                if (this.fluidTank.getOutputHandler().getStorageTank(0).isFull()) return;
                this.fluidTank.getOutputHandler().forceFill(input.copy(), IFluidHandler.FluidAction.EXECUTE);
                input.shrink(input.getAmount());
                running = false;
                fermentationTicks = 0;
                recipeInventoryWrapper = null;
            }
            this.setChanged();
        }
    }

    private void writeNBTtoFluidStack(FluidStack stack) {
        FermentationHelper.ageFluidStack(stack, this.fermentationTicks);
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("fermentationTicks", this.fermentationTicks);
        pTag.putFloat("catalystUsed", this.cost);
        pTag.putBoolean("running", this.running);
        pTag.put("fluidHandler", this.fluidTank.serializeNBT());
        pTag.put("itemHandler", this.inventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.fermentationTicks = pTag.getInt("fermentationTicks");
        this.running = pTag.getBoolean("running");
        this.fluidTank.deserializeNBT(pTag.getCompound("fluidHandler"));
        this.inventory.deserializeNBT(pTag.getCompound("itemHandler"));
        this.cost = pTag.getFloat("catalystUsed");
    }

    public float getCost() {
        return cost;
    }

    public float getCatalystAmount() {
        return catalystAmount;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public boolean tryMergeFluid(Level pLevel, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        if (FluidUtil.getFluidHandler(pPlayer.getItemInHand(pHand)).isPresent()) {
//            if (!this.fluidTank.getInputHandler().getFluidInTank(0).isEmpty()) {
//                if (this.fluidTank.getInputHandler().getFluidInTank(0).isFluidEqual())
//            }
//        }
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventory.getHandler(side);
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidTank.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(String.format("menu.%s.wooden_barrel", Brewchemy.MODID));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FermentationBarrelMenu(pContainerId, pInventory, pPlayer, this);
    }
}
