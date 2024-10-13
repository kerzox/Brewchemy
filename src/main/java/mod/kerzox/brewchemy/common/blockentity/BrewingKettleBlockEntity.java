package mod.kerzox.brewchemy.common.blockentity;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.brewchemy.client.ui.animation.brewing.BrewingKettleAnimationHandler;
import mod.kerzox.brewchemy.client.ui.menu.BrewingMenu;
import mod.kerzox.brewchemy.common.blockentity.base.RecipeBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.DynamicMultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidInventory;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.recipe.BrewingRecipe;
import mod.kerzox.brewchemy.common.data.BrewingKettleHeating;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.network.CompoundTagPacket;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class BrewingKettleBlockEntity extends RecipeBlockEntity<BrewingRecipe> implements MenuProvider {

    /**
     * Modified fluid inventory from base
     * Idea is that the kettle has a maximum capacity of 16000 for all tanks including the 2 inputs and 1 output
     * need to override the dynamic output to follow the capacity of the 2 inputs
     */
    private final MultifluidInventory fluidHandler =
            new MultifluidInventory(
                    DynamicMultifluidTank.of(2, 16000),
                    new DynamicMultifluidTank(1, 16000) {

                        @Override
                        public int getTankCapacity(int tank) {
                            return fluidHandler.getInputWrapper().getTankCapacity(tank);
                        }

                        @Override
                        public int getRemainingCapacity(int tank) {
                            return ((DynamicMultifluidTank)fluidHandler.getInputWrapper().get()).getRemainingCapacity(tank);
                        }

                    })
                    .addInput(Direction.values()).removeInputs(Direction.DOWN).addOutput(Direction.DOWN);
    private final ItemInventory itemHandler = new ItemInventory(new ItemInventory.InternalWrapper(2, true), new ItemInventory.InternalWrapper(1, false)) {
        @Override
        public void onContentsChanged(int slot, boolean input) {
            ItemStack serverStack = getStackInSlot(slot);
            screenData.contentChanged(slot, this.getInputHandler().serializeNBT());
            checkRecipe();
        }
    };

    protected ScreenData screenData = new ScreenData();
    protected boolean updateInventoryOnClientScreen;
    protected int heat = 0;
    protected int temperatureValue = 3;

    // in seconds
    protected int timeToRoomTemperature = 3;
    protected int timeToChangeFromSource = 2;

    protected CompoundTag clientDataTag = new CompoundTag();

    public BrewingKettleBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.BREWING_KETTLE_BLOCK_ENTITY.get(), BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pos, state);
        addCapabilities(itemHandler, fluidHandler);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {

        if (!pLevel.isClientSide) {
            pPlayer.sendSystemMessage(Component.literal("Heat value: " + this.heat));
        }

        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public RecipeInventory getRecipeInventory() {
        return new RecipeInventory(itemHandler, fluidHandler, (r) -> canProgress((BrewingRecipe) r));
    }

    @Override
    protected boolean hasAResult(BrewingRecipe workingRecipe) {
        return workingRecipe.assembleResultItems(getRecipeInventory(), RegistryAccess.EMPTY).length > 0;
    }

    @Override
    protected void onRecipeFinish(BrewingRecipe workingRecipe) {
        FluidStack[] result = workingRecipe.assembleResultItems(getRecipeInventory(), RegistryAccess.EMPTY);

        /*
            Create a temp input handler we can use the ingredients and check if we can output the recipe
            After that we use the ingredients
         */
        IFluidHandler temp = fluidHandler.getInputWrapper().get().copy();
        useFluidIngredients(workingRecipe.getFluidIngredients(), temp);

        if (hasEnoughFluidSlots(result, fluidHandler.getOutputWrapper()).isEmpty()) return;

        useFluidIngredients(workingRecipe.getFluidIngredients(), fluidHandler.getInputWrapper());
        useSizeSpecificIngredients(workingRecipe.getIngredients(), itemHandler.getInputHandler());
        // if we get here than transfer the fluid results and use the ingredients
        transferFluidResults(result, fluidHandler.getOutputWrapper());


        finishRecipe();
    }

    @Override
    public void tick() {
        super.tick();

        int maxHeatFromSource = BrewingKettleHeating.getHeat(level.getBlockState(worldPosition.below()).getBlock());

        // if heat source is 0 we want to return to 0 heat
        if (maxHeatFromSource == 0) {
            // if the heat source is 0 check every x seconds to return heat value to 0
            if (TickUtils.every(tick, timeToRoomTemperature) && this.heat != 0) {
                this.heat = this.heat > 0 ? Math.max(0, this.heat - temperatureValue) : Math.min(0, this.heat + temperatureValue);
            }
        } else {
            // change heat value overtime to source heat
            if (TickUtils.every(tick, timeToChangeFromSource) && this.heat != maxHeatFromSource) {
                this.heat = this.heat < maxHeatFromSource ? Math.min(maxHeatFromSource, this.heat + temperatureValue) : Math.max(maxHeatFromSource, this.heat - temperatureValue);
            }
        }

    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.updateInventoryOnClientScreen = true;
    }

    public boolean hasSavedData() {
        return updateInventoryOnClientScreen;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("client_data", this.clientDataTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.clientDataTag = pTag.getCompound("client_data");
    }

    @Override
    protected void write(CompoundTag pTag) {
        super.write(pTag);
        pTag.putInt("heat", this.heat);
        pTag.putBoolean("update_client", this.updateInventoryOnClientScreen);
        pTag.put("screen_data", this.screenData.writeServerTag());
    }

    @Override
    protected void read(CompoundTag pTag) {
        super.read(pTag);
        this.heat = pTag.getInt("heat");
        this.screenData.readServerUpdate(pTag.getCompound("screen_data"));
    }

    @Override
    public void onMenuSync(ServerPlayer playerInteracting, int state) {
        if (state == 0) { // state 0 is when a player opens the menu
            PacketHandler.sendToClientPlayer(new CompoundTagPacket(clientDataTag), playerInteracting);
        }
    }

    @Override
    protected boolean canProgress(BrewingRecipe workingRecipe) {
        return workingRecipe.getHeat() < 0 ? workingRecipe.getHeat() >= this.heat : workingRecipe.getHeat() <= this.heat;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Brewing Kettle");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new BrewingMenu(p_39954_, p_39955_, p_39956_, this);
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        if (tag.contains("client")) {
            this.clientDataTag = tag.getCompound("client");
        } else if (tag.contains("client_update")) {
            screenData.setClientTag(tag.getCompound("client_update"));
            screenData.readScreenData();
        }
    }

    public int getHeat() {
        return this.heat;
    }

    public ScreenData getScreenData() {
        return screenData;
    }

    public class ScreenData {

        protected NonNullList<BrewingKettleAnimationHandler> itemStackAnimationStates = NonNullList.withSize(2, new BrewingKettleAnimationHandler());
        protected NonNullList<ItemStack> clientInventory = NonNullList.withSize(2, ItemStack.EMPTY);
        protected Stack<Pair<Integer, ItemStack>> newItems = new Stack<>();
        protected CompoundTag clientTag = new CompoundTag();
        protected Stack<Integer> slotsChanged = new Stack<>();
        private CompoundTag serverInventoryTag = new CompoundTag();

        public ScreenData() {
            itemStackAnimationStates.set(0, new BrewingKettleAnimationHandler());
            itemStackAnimationStates.set(1, new BrewingKettleAnimationHandler());
        }

        public void setClientTag(CompoundTag clientTag) {
            this.clientTag = clientTag;
        }

        public NonNullList<BrewingKettleAnimationHandler> getItemStackAnimationStates() {
            return itemStackAnimationStates;
        }

        public NonNullList<ItemStack> getClientInventory() {
            return clientInventory;
        }

        // only use this on the client
        public Stack<Pair<Integer, ItemStack>> getToSpawnOnClient() {
            return newItems;
        }

        public CompoundTag writeServerTag() {
            CompoundTag tag = new CompoundTag();
            ListTag slots = new ListTag();
            while (!slotsChanged.isEmpty()) {
                CompoundTag tag1 = new CompoundTag();
                tag1.putInt("slot", slotsChanged.pop());
                slots.add(tag1);
            }
            tag.put("server_inventory_tag", serverInventoryTag);
            tag.put("slots_changed", slots);
            return tag;
        }

        public void readServerUpdate(CompoundTag tag) {
            ListTag spawnList = tag.getList("slots_changed", Tag.TAG_COMPOUND);
            for (int i = 0; i < spawnList.size(); i++) {
                CompoundTag item = spawnList.getCompound(i);
                int slot = item.getInt("slot");

                BrewingKettleBlockEntity.this.itemHandler.getInputHandler().deserializeNBT(tag.getCompound("server_inventory_tag"));

                ItemStack serverStack = BrewingKettleBlockEntity.this.itemHandler.getStackInSlot(slot);
                ItemStack clientStack = screenData.clientInventory.get(slot);

                if (!serverStack.is(clientStack.getItem())) {
                    screenData.spawnItem(slot, serverStack);
                } else if (serverStack.getCount() != clientStack.getCount()) {
                    clientStack.setCount(serverStack.getCount());
                }

            }
        }

        /**
         * Call on the client to save the current state
         *
         * @return the saved tag
         */
        public CompoundTag saveScreenData() {
            CompoundTag tag = new CompoundTag();

            ListTag animationStates = new ListTag();
            for (BrewingKettleAnimationHandler handlers : itemStackAnimationStates) {
                animationStates.add(handlers.saveToTag());
            }

            ListTag nbtTagList = new ListTag();
            for (int i = 0; i < clientInventory.size(); i++) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                clientInventory.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }

            tag.put("animation_states", animationStates);
            tag.put("client_inventory", nbtTagList);

            return tag;
        }

        /**
         * Reads the client saved data tag
         */
        public void readScreenData() {

            ListTag animationStates = getClientTag().getList("animation_states", Tag.TAG_COMPOUND);
            for (int i = 0; i < animationStates.size(); i++) {
                itemStackAnimationStates.get(i).readTag(animationStates.getCompound(i));
            }
            ListTag clientInv = getClientTag().getList("client_inventory", Tag.TAG_COMPOUND);
            for (int i = 0; i < clientInv.size(); i++) {
                CompoundTag itemTags = clientInv.getCompound(i);
                int slot = itemTags.getInt("Slot");
                clientInventory.set(slot, ItemStack.of(itemTags));
            }

            for (int i = 0; i < clientInventory.size(); i++) {
                itemStackAnimationStates.get(i).setItemStack(clientInventory.get(i));
            }

        }

        public void spawnItem(int slot, ItemStack serverStack) {
            newItems.add(Pair.of(slot, serverStack));
        }

        public void removeItem(int slot) {

        }

        public CompoundTag getClientTag() {
            return clientTag;
        }

        public void contentChanged(int slot, CompoundTag compoundTag) {
            slotsChanged.add(slot);
            serverInventoryTag = compoundTag;
        }
    }

}
