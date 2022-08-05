package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.menu.MillstoneMenu;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.recipes.MillstoneRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MillStoneBlockEntity extends BrewchemyBlockEntity implements IServerTickable, MenuProvider {

    private final ItemStackInventory inventory = new ItemStackInventory(1, 1);

    private int recipeDuration;
    private boolean running;

    public MillStoneBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.MILL_STONE.get(), pWorldPosition, pBlockState);
        this.inventory.addInput(Direction.WEST, Direction.EAST);
        this.inventory.addOutput(Direction.DOWN);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.inventory.getHandler(side);
        }
        return super.getCapability(cap, side);
    }


    public int updateProgress() {
        if (running) {
            this.recipeDuration--;
        }
        return this.recipeDuration;
    }

    @Override
    public void onServer() {
        checkForRecipes();
    }

    private void checkForRecipes() {
        RecipeInventoryWrapper recipeInv = null;
        // check for items
        for (int i = 0; i < inventory.getInputHandler().getSlots(); i++) {
            if (!inventory.getInputHandler().getStackInSlot(i).isEmpty()) {
                recipeInv = new RecipeInventoryWrapper(this.inventory.getInputHandler());
            }
        }
        // if no items exit
        if (recipeInv == null) return;
        // check if recipe can be found from items
        Optional<MillstoneRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.MILLSTONE_RECIPE.get(), recipeInv, level);
        if (recipe.isPresent()) {
            doRecipe(recipe.get(), recipeInv);
        }
    }

    private void doRecipe(MillstoneRecipe recipe, RecipeInventoryWrapper recipeInventoryWrapper) {
        ItemStack result = recipe.assemble(recipeInventoryWrapper);
        
        if (!result.isEmpty()) {
            if (!running) {
                recipeDuration = recipe.getDuration();
                running = true;
            }
            if (recipeDuration <= 0) {
                running = false;
                ItemStack simulation = this.inventory.getInputHandler().forceExtractItem(0, 1, true);
                if (!simulation.isEmpty()) {
                    if (this.inventory.getOutputHandler().forceInsertItem(0, result, true).isEmpty()) {
                        this.inventory.getInputHandler().getStackInSlot(0).shrink(1);
                        this.inventory.getOutputHandler().forceInsertItem(0, result, false);
                    }
                }
            }
        }

    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("itemHandler", this.inventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.inventory.deserializeNBT(pTag.getCompound("itemHandler"));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(String.format("menu.%s.millstone", Brewchemy.MODID));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new MillstoneMenu(pContainerId, pInventory, pPlayer, this);
    }

}
