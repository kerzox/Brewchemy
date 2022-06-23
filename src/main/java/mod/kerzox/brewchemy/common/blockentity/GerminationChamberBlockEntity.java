package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.recipes.GerminationRecipe;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class GerminationChamberBlockEntity extends BrewchemyBlockEntity implements IServerTickable, MenuProvider {

    private final ItemStackInventory inventory = new ItemStackInventory(new ItemStackInventory.InputHandler(6) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }, new ItemStackInventory.OutputHandler(1));

    private boolean reCache;
    
    private Map<ItemStack, Integer> inputSlots = new HashMap<>();
    private Map<ItemStack, GerminationRecipe> cachedRecipes = new HashMap<>();
    private Map<ItemStack, Integer> recipeDurations = new HashMap<>();

    public GerminationChamberBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.GERMINATION_CHAMBER.get(), pWorldPosition, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.inventory.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onServer() {
        checkInputs();
        if (reCache) checkAndCacheRecipes();
        doAllRecipes();
    }

    private void doAllRecipes() {
        for (Iterator<Map.Entry<ItemStack, GerminationRecipe>> iterator = cachedRecipes.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<ItemStack, GerminationRecipe> entry = iterator.next();
            ItemStack ingredient = entry.getKey();
            GerminationRecipe recipe = entry.getValue();

            RecipeInventoryWrapper recipeInv = new RecipeInventoryWrapper(new ItemStackHandler(1));
            recipeInv.setItem(0, ingredient);
            ItemStack result = recipe.assemble(recipeInv);

            if (result.isEmpty()) {
                iterator.remove();
            }

            recipeDurations.computeIfAbsent(ingredient, k -> recipe.getDuration());

            if (canGerminate()) {
                int duration = this.recipeDurations.get(ingredient);
                duration--;
                recipeDurations.put(ingredient, duration);
            }

            if (recipeDurations.get(ingredient) <= 0) {
                if (inputSlots.get(ingredient) == null) return;
                ItemStack simulation = this.inventory.getInputHandler().forceExtractItem(inputSlots.get(ingredient), 1, true);
                if (!simulation.isEmpty()) {
                    if (this.inventory.getOutputHandler().forceInsertItem(0, result, true).isEmpty()) {
                        ItemStack stack = this.inventory.getStackFromInputHandler(inputSlots.get(ingredient));
                        this.inputSlots.remove(stack);
                        recipeDurations.remove(ingredient);
                        stack.shrink(1);
                        this.inventory.getOutputHandler().forceInsertItem(0, result, false);
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void checkInputs() {
        Map<ItemStack, Integer> temp = new HashMap<>(inputSlots);
        for (int i = 0; i < this.inventory.getInputHandler().getSlots(); i++) {
            if (!this.inventory.getStackFromInputHandler(i).isEmpty())
            {
                temp.put(this.inventory.getStackFromInputHandler(i), i);
            }
        }
        if (!temp.equals(inputSlots)) {
            reCache = true;
            for (Map.Entry<ItemStack, Integer> itemStackIntegerEntry : temp.entrySet()) {
                inputSlots.put(itemStackIntegerEntry.getKey(), itemStackIntegerEntry.getValue());
            }
        }
    }
    
    private void checkAndCacheRecipes() {
        for (ItemStack item : inputSlots.keySet()) {
            if (cachedRecipes.get(item) != null) continue;
            RecipeInventoryWrapper recipeInv = new RecipeInventoryWrapper(new ItemStackHandler(1));
            recipeInv.setItem(0, item);

            Optional<GerminationRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.GERMINATION_RECIPE.get(),
                    recipeInv, level);

            recipe.ifPresent(germinationRecipe -> this.cachedRecipes.put(item, germinationRecipe));
        }
    }

    private boolean canGerminate() {
        return level.canSeeSky(getBlockPos().above());
    }


    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(String.format("menu.%s.germination_chamber", Brewchemy.MODID));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new GerminationChamberMenu(pContainerId, pInventory, pPlayer, this);
    }
}
