package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GerminationChamberBlockEntity extends BrewchemyBlockEntity implements IServerTickable, MenuProvider {
    public GerminationChamberBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {

    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }
//
//    private final ItemStackInventory inventory = new ItemStackInventory(new ItemStackInventory.InputHandler(6) {
//        @Override
//        public int getSlotLimit(int slot) {
//            return 1;
//        }
//    }, new ItemStackInventory.OutputHandler(1));
//
//    private boolean reCache;
//    private boolean hasSun;
//
//    private Map<ItemStack, Integer> inputSlots = new HashMap<>();
//    private Map<ItemStack, GerminationRecipe> cachedRecipes = new HashMap<>();
//    private Map<ItemStack, Integer> recipeDurations = new HashMap<>();
//
//    public GerminationChamberBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
//        super(BrewchemyRegistry.BlockEntities.GERMINATION_CHAMBER.get(), pWorldPosition, pBlockState);
//    }
//
//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            return this.inventory.getHandler(side);
//        }
//        return super.getCapability(cap, side);
//    }
//
//    @Override
//    public void onServer() {
//        checkInputs();
//        if (reCache) checkAndCacheRecipes();
//        doAllRecipes();
//        syncBlockEntity();
//    }
//
//    private void doAllRecipes() {
//        for (Iterator<Map.Entry<ItemStack, GerminationRecipe>> iterator = cachedRecipes.entrySet().iterator(); iterator.hasNext(); ) {
//            Map.Entry<ItemStack, GerminationRecipe> entry = iterator.next();
//            ItemStack ingredient = entry.getKey();
//            GerminationRecipe recipe = entry.getValue();
//
//            RecipeInventoryWrapper recipeInv = new RecipeInventoryWrapper(new ItemStackHandler(1));
//            recipeInv.setItem(0, ingredient);
//            ItemStack result = recipe.assemble(recipeInv);
//
//            if (result.isEmpty()) {
//                iterator.remove();
//            }
//
//            recipeDurations.computeIfAbsent(ingredient, k -> recipe.getDuration());
//
//            if (canGerminate()) {
//                int duration = this.recipeDurations.get(ingredient);
//                duration--;
//                recipeDurations.put(ingredient, duration);
//                syncBlockEntity();
//            }
//
//            if (recipeDurations.get(ingredient) <= 0) {
//                if (inputSlots.get(ingredient) == null) return;
//                ItemStack simulation = this.inventory.getInputHandler().forceExtractItem(inputSlots.get(ingredient), 1, true);
//                if (!simulation.isEmpty()) {
//                    if (this.inventory.getOutputHandler().forceInsertItem(0, result, true).isEmpty()) {
//                        ItemStack stack = this.inventory.getStackFromInputHandler(inputSlots.get(ingredient));
//                        this.inputSlots.remove(stack);
//                        recipeDurations.remove(ingredient);
//                        stack.shrink(1);
//                        this.inventory.getOutputHandler().forceInsertItem(0, result, false);
//                        iterator.remove();
//                    }
//                }
//            }
//        }
//    }
//
//    private void checkInputs() {
//        Map<ItemStack, Integer> temp = new HashMap<>(inputSlots);
//        for (int i = 0; i < this.inventory.getInputHandler().getSlots(); i++) {
//            if (!this.inventory.getStackFromInputHandler(i).isEmpty())
//            {
//                temp.put(this.inventory.getStackFromInputHandler(i), i);
//            }
//        }
//        if (!temp.equals(inputSlots)) {
//            reCache = true;
//            for (Map.Entry<ItemStack, Integer> itemStackIntegerEntry : temp.entrySet()) {
//                inputSlots.put(itemStackIntegerEntry.getKey(), itemStackIntegerEntry.getValue());
//            }
//        }
//    }
//
//    private void checkAndCacheRecipes() {
//        for (Iterator<ItemStack> it = inputSlots.keySet().iterator(); it.hasNext(); ) {
//            ItemStack item = it.next();
//            if (cachedRecipes.get(item) != null) continue;
//            RecipeInventoryWrapper recipeInv = new RecipeInventoryWrapper(new ItemStackHandler(1));
//            recipeInv.setItem(0, item);
//            Optional<GerminationRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewchemyRegistry.Recipes.GERMINATION_RECIPE.get(),
//                    recipeInv, level);
//            if (recipe.isPresent()) {
//                this.cachedRecipes.put(item, recipe.get());
//                return;
//            }
//            it.remove();
//        }
//    }
//
//    public boolean canGerminate() {
//        if (level == null) return false;
//        return level.isDay() && level.canSeeSky(getBlockPos().above());
//    }
//
//    @Override
//    protected void write(CompoundTag pTag) {
//
//    }
//
//    @Override
//    protected void read(CompoundTag pTag) {
//        hasSun = pTag.getBoolean("hasSun");
//    }
//
//    @Override
//    protected void addToUpdateTag(CompoundTag tag) {
//        if (level.isClientSide) {
//            tag.putBoolean("hasSun", hasSun);
//        }else {
//            tag.putBoolean("hasSun", canGerminate());
//        }
//    }
//
//    @Override
//    public @NotNull Component getDisplayName() {
//        return Component.translatable(String.format("menu.%s.germination_chamber", Brewchemy.MODID));
//    }
//
//    @Nullable
//    @Override
//    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
//        return new GerminationChamberMenu(pContainerId, pInventory, pPlayer, this);
//    }
}
