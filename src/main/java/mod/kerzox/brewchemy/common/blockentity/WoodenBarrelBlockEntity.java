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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WoodenBarrelBlockEntity extends BrewchemyBlockEntity implements IServerTickable, MenuProvider {

    private final ItemStackInventory inventory = new ItemStackInventory(1, 1);
    private final SidedMultifluidTank fluidTank = new SidedMultifluidTank(1, PintGlassItem.KEG_VOLUME, 1, PintGlassItem.KEG_VOLUME);
    private final LazyOptional<SidedMultifluidTank> handler = LazyOptional.of(() -> fluidTank);
    private final FluidStack[] inputStacks = new FluidStack[]{FluidStack.EMPTY};

    private boolean running;
    private int fermentationTicks;
    private int tick;

    private boolean blockFermentation;

    public WoodenBarrelBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.WOODEN_BARREL.get(), pWorldPosition, pBlockState);
    }

    public SidedMultifluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public void onServer() {
        if (this.fluidTank.getFluidInTank(0).isEmpty()) {
            return;
        }

        if (!blockFermentation) {
            Optional<FermentationRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), new RecipeInventoryWrapper(this.fluidTank, inventory), level);
            recipe.ifPresent(this::doRecipe);
        }

    }

    public boolean isFermentationBlocked() {
        return blockFermentation;
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
        pTag.put("fluidHandler", this.getFluidTank().serialize());
        pTag.putBoolean("running", this.running);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.fermentationTicks = pTag.getInt("fermentationTicks");
        this.running = pTag.getBoolean("running");
        if (pTag.contains("fluidHandler")) this.getFluidTank().deserialize(pTag);
    }

    // we need to merge stacks together!

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public boolean tryMergeFluid(Level pLevel, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (FluidUtil.getFluidHandler(pPlayer.getItemInHand(pHand)).isPresent()) {
            for (int i = 0; i < this.fluidTank.getInputHandler().getTanks(); i++) {
                FluidStack stack1 = new FluidStack(this.fluidTank.getInputHandler().getFluidInTank(i).getFluid(), this.fluidTank.getInputHandler().getFluidInTank(i).getAmount());
                if (stack1.getAmount() == PintGlassItem.KEG_VOLUME) {
                    return false;
                }
                blockFermentation = true;
                running = false;
                FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, getBlockPos(), pHit.getDirection());
                blockFermentation = false;
            }
        }
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventory.getHandler(side);
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
