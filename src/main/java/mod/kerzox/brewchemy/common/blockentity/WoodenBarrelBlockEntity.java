package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.menu.FermentationBarrelMenu;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedMultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.BrewingRecipe;
import mod.kerzox.brewchemy.common.crafting.recipes.FermentationRecipe;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.common.util.FermentationHelper;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class WoodenBarrelBlockEntity extends BrewchemyBlockEntity implements IServerTickable, MenuProvider {

    private final ItemStackInventory inventory = new ItemStackInventory(1, 1);
    private final SidedMultifluidTank fluidTank = new SidedMultifluidTank(1, PintGlassItem.KEG_VOLUME, 1, PintGlassItem.KEG_VOLUME);
    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> fluidTank);
    private final FluidStack[] inputStacks = new FluidStack[]{FluidStack.EMPTY};

    private boolean running;
    private int fermentationTicks;
    private int tick;

    public WoodenBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.WOODEN_BARREL.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {
        tick++;
        for (int i = 0; i < this.fluidTank.getTanks(); i++) {
            if (tick % 20 == 0) {
                System.out.println(this.fluidTank.getFluidInTank(i).getAmount());
            }
        }
        if (this.fluidTank.getFluidInTank(0).isEmpty()) {
            inputStacks[0] = FluidStack.EMPTY;
            return;
        }
        if (inputStacks[0].isEmpty()) {
            inputStacks[0] = this.fluidTank.getFluidInTank(0);
        } else if (!inputStacks[0].isFluidStackIdentical(this.fluidTank.getFluidInTank(0))) {
            this.running = false;
            this.inputStacks[0] = this.fluidTank.getFluidInTank(0);
        }
        Optional<FermentationRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), new RecipeInventoryWrapper(this.fluidTank, inventory), level);
        recipe.ifPresent(this::doRecipe);
    }

    public int getTick() {
        return tick;
    }

    private void doRecipe(FermentationRecipe recipe) {
        FluidStack result = recipe.assembleFluid(new RecipeInventoryWrapper(this.fluidTank, inventory));
        if (result.isEmpty()) return;
        if (!running) {
            fermentationTicks = 0;
            running = true;
        }
        fermentationTicks++;
        FluidStack input = this.fluidTank.getFluidInTank(0);
        writeNBTtoFluidStack(input);
        if (FermentationHelper.getFermentationStage(input) == FermentationHelper.Stages.MATURE) {
            if (this.fluidTank.getOutputHandler().forceFill(input, IFluidHandler.FluidAction.SIMULATE) != 0) {
                this.fluidTank.getOutputHandler().forceFill(this.fluidTank.getInputHandler().forceDrain(input, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
            }
            running = false;
        }
        this.setChanged();

    }

    private void writeNBTtoFluidStack(FluidStack stack) {
        FermentationHelper.ageFluidStack(stack, this.fermentationTicks);
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("fermentationTicks", this.fermentationTicks);
        pTag.putBoolean("running", this.running);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.fermentationTicks = pTag.getInt("fermentationTicks");
        this.running = pTag.getBoolean("running");
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {

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
