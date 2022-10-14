package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.block.BoilKettleBlock;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedMultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.CountSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.OldFluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.BrewingRecipe;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.BlockEntities.BREWING_TOP_POT;

public class BoilKettleBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    private final ItemStackInventory.InputHandler inventory = new ItemStackInventory.InputHandler(5) {

        @Override
        protected void onContentsChanged(int slot) {
            if (getLevel() != null && workingRecipe != null) {
                workingRecipe = workingRecipe.matches(recipeInventoryWrapper, level) ? workingRecipe : null;
            }
            syncBlockEntity();
        }

    };

    private final SidedMultifluidTank sidedFluidTank = new SidedMultifluidTank(1, PintGlassItem.KEG_VOLUME, 1, PintGlassItem.KEG_VOLUME) {

        @Override
        protected void onContentsChanged(IFluidHandler handlerAffected) {
            if (getLevel() != null && workingRecipe != null) {
                workingRecipe = workingRecipe.matches(recipeInventoryWrapper, level) ? workingRecipe : null;
            }
            syncBlockEntity();
        }

    };

    private final RecipeInventoryWrapper recipeInventoryWrapper = new RecipeInventoryWrapper(sidedFluidTank, inventory, true);
    private final LazyOptional<ItemStackHandler> itemHandler = LazyOptional.of(() -> inventory);
    private boolean running = false;
    private int duration;
    private int heat;
    private int currentRecipeHeat;
    private int tick;
    private boolean stateChanged = false;

    private BrewingRecipe workingRecipe;

    public BoilKettleBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.BREWING_POT.get(), pWorldPosition, pBlockState);
    }

    private int getHeatSource() {
        if (level.getBlockState(this.getBlockPos().below()).getBlock() instanceof BaseFireBlock) {
            return level.getBlockState(this.getBlockPos().below()).getBlock() instanceof SoulFireBlock ? BrewingRecipe.SUPERHEATED : BrewingRecipe.FIRE;
        }
        else if (level.getBlockState(this.getBlockPos().below()).getBlock() instanceof CampfireBlock) {
            return BrewingRecipe.FIRE;
        }
        return BrewingRecipe.NO_HEAT;
    }

    @Override
    public void onServer() {
        tick++;
        calculateHeat();
        // when lid is open allow items to be inputted
        if (!level.getBlockState(getBlockPos()).getValue(BoilKettleBlock.LID)) {
            double x = getBlockPos().above().getX(), y = getBlockPos().above().getY(), z = getBlockPos().above().getZ();
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AABB(x + .1, y - 1, z + .1, x + .85, y + .5, z + .85), EntitySelector.ENTITY_STILL_ALIVE);
            for (Entity entity : entities) {
                if (entity instanceof ItemEntity itemEntity) {
                    ItemStack stack = itemEntity.getItem().copy();
                    for (int i = 0; i < this.inventory.getSlots(); i++) {
                        if (this.inventory.insertItem(i, stack, true).isEmpty()) {
                            itemEntity.discard();
                            this.inventory.insertItem(i, stack, false);
                            break;
                        }
                    }
                }
            }
        }
        if (workingRecipe != null) doRecipe(workingRecipe);
        else {
            this.findValidRecipe(recipeInventoryWrapper).ifPresent(this::doRecipe);
        }
   }

    private void calculateHeat() {
        int pastH = heat;
        if (heat < getHeatSource()) {
            heat++;
        } else {
            if (heat > 0) {
                if (tick % 20 == 0) { // loses heat every second
                    heat--;
                }
            }
        }

        if (heat != pastH) {
            syncBlockEntity();
        }
    }

    public Optional<BrewingRecipe> findValidRecipe(RecipeInventoryWrapper wrapper) {
        return level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), wrapper, level);
    }

    public void doRecipe(BrewingRecipe recipe) {
        FluidStack result = recipe.assembleFluid(null);
        if (result.isEmpty()) return;
        if (!hasHeatForRecipe(recipe)) return;
        if (!running) {
            workingRecipe = recipe;
            duration = recipe.getDuration();
            running = true;
        }
        if (duration <= 0) {
            // just make sure
            if (!workingRecipe.matches(recipeInventoryWrapper, level)) {
                running = false;
                workingRecipe = null;
                return;
            }

            if (!this.sidedFluidTank.getOutputHandler().getStorageTank(0).isEmpty()) {
                if (!this.sidedFluidTank.getOutputHandler().getStorageTank(0).getFluid().isFluidEqual(result)) return;
            }

            for (SizeSpecificIngredient ingredient : recipe.getSizedIngredients()) {
                for (int i = 0; i < this.inventory.getSlots(); i++) {
                    if (ingredient.test(this.inventory.getStackInSlot(i))) {
                        this.inventory.getStackInSlot(i).shrink(ingredient.getSize());
                    }
                }
            }

            recipe.getFluidIngredient().drain(sidedFluidTank.getInputHandler().getFluidInTank(0), false);
            sidedFluidTank.getOutputHandler().forceFill(result, IFluidHandler.FluidAction.EXECUTE);
            running = false;
            if (workingRecipe != null) workingRecipe = workingRecipe.matches(recipeInventoryWrapper, level) ? workingRecipe : null;
        }
        duration--;
        syncBlockEntity();
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            if (pPlayer.isShiftKeyDown()) {
                for (int i = 0; i < this.inventory.getSlots(); i++) {
                    if (!this.inventory.getStackInSlot(i).isEmpty()) {
                        BlockPos relative = pPos.relative(pPlayer.getDirection().getOpposite(), 2);
                        level.addFreshEntity(new ItemEntity(level, relative.getX(), relative.getY(), relative.getZ(), this.inventory.forceExtractItem(i, 64, false)));
                    }
                }
            }
            else if (getBlockState().getBlock() instanceof BoilKettleBlock kettle) {
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
        pTag.put("fluidHandler", this.sidedFluidTank.serializeNBT());
        pTag.put("itemHandler", this.inventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
       this.duration = pTag.getInt("duration");
       this.heat = pTag.getInt("heat");
       this.currentRecipeHeat = pTag.getInt("recipeHeat");
       this.sidedFluidTank.deserializeNBT(pTag.getCompound("fluidHandler"));
       this.inventory.deserializeNBT(pTag.getCompound("itemHandler"));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.sidedFluidTank.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

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

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    public static class TopBlockEntity extends BrewchemyBlockEntity {

        private BoilKettleBlockEntity body;

        public TopBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
            super(BREWING_TOP_POT.get(), pWorldPosition, pBlockState);
        }

        @Override
        public void onLoad() {
            super.onLoad();
            if (level.getBlockEntity(getBlockPos().below()) instanceof BoilKettleBlockEntity boilKettleBlockEntity) {
                setBody(boilKettleBlockEntity);
            }
        }

        public void setBody(BoilKettleBlockEntity body) {
            this.body = body;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (body != null) return body.getCapability(cap, side);
            return super.getCapability(cap, side);
        }
    }

}
