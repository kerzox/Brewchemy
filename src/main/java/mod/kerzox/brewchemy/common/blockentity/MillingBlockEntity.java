package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.client.ui.menu.MillingMenu;
import mod.kerzox.brewchemy.common.blockentity.base.RecipeBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.energy.EnergyInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.recipe.MillingRecipe;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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
import org.jetbrains.annotations.Nullable;

public class MillingBlockEntity extends RecipeBlockEntity<MillingRecipe> implements MenuProvider {

    private final EnergyInventory energyHandler = EnergyInventory.of(10000).addInput(Direction.values());
    private final ItemInventory itemStackHandler = ItemInventory.of(1, 1).addInput(Direction.values()).addOutput(Direction.DOWN);

    /*
        TODO
        Add config for the Fe/Tick usage of the milling block
     */

    private int FE_TICK = 20;

    public MillingBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.MILLING_BLOCK_ENTITY.get(), BrewchemyRegistry.Recipes.MILLING_RECIPE.get(), pos, state);
        addCapabilities(itemStackHandler, energyHandler);
    }

    @Override
    public RecipeInventory getRecipeInventory() {
        return new RecipeInventory(this.itemStackHandler);
    }

    @Override
    protected boolean hasAResult(MillingRecipe workingRecipe) {
        return !workingRecipe.assemble(getRecipeInventory(), RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(MillingRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(getRecipeInventory(), RegistryAccess.EMPTY);

        if (hasEnoughItemSlots(new ItemStack[]{result}, itemStackHandler.getOutputHandler()).size() != 1) return;
        transferItemResults(new ItemStack[]{result}, itemStackHandler.getOutputHandler());

        useIngredients(NonNullList.withSize(1, workingRecipe.getIngredients().get(0)), itemStackHandler.getInputHandler(), 1);

        finishRecipe();
    }

    /*
        Milling
        Requires either forge energy to progress a recipe or a player providing hunger.
        Player holding right click will "power" the milling machine.
     */

    @Override
    protected boolean canProgress(MillingRecipe workingRecipe) {

        if (energyHandler.hasEnough(FE_TICK)) {
            energyHandler.consumeEnergy(FE_TICK);
            return true;
        }

        return false;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {

        if (pHand == InteractionHand.MAIN_HAND && !pLevel.isClientSide && pPlayer.isShiftKeyDown()) {
            pPlayer.causeFoodExhaustion(1F);
            energyHandler.addEnergy(FE_TICK);
            this.recipeDuration -= 5;
            return true;
        }

        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Milling");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new MillingMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
