package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.CapabilityBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.base.SyncedBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidInventoryItem;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackHandlerUtils;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PintGlassBlockEntity extends CapabilityBlockEntity {

    private final ItemInventory itemStackHandler = new ItemInventory(
            new ItemInventory.InternalWrapper(2, true),
            new ItemInventory.InternalWrapper(1, false)
    ) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (!stack.is(BrewchemyRegistry.Items.PINT_ITEM.get())) return false;
            return super.isItemValid(slot, stack);
        }
    };

    public List<ItemStack> getBeers() {
        return itemStackHandler.getInputHandler().getStacks();
    }

    public PintGlassBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.PINT_GLASS_BLOCK_ENTITY.get(), pos, state);
        addCapabilities(itemStackHandler);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand != InteractionHand.MAIN_HAND) return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
        ItemStack itemInHand = pPlayer.getItemInHand(pHand);
        Direction hitDirection = pHit.getDirection();
        Direction blockFacing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        // try to put the item stack into the inventory
        if (!itemInHand.isEmpty() && itemInHand.is(BrewchemyRegistry.Items.PINT_ITEM.get())) {

            if (!ItemHandlerHelper.insertItem(itemStackHandler.getInputHandler(), itemInHand, true).isEmpty()) {
                return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
            }

            ItemStack pintAlreadyInside = itemStackHandler.getInputHandler().internalExtractItem(0, 1, false);
            itemStackHandler.getInputHandler().insertItem(0, itemInHand.copy(), false);
            itemStackHandler.getInputHandler().insertItem(1, pintAlreadyInside, false);
            itemInHand.shrink(1);


        } else {

            if (getBeers().stream().filter(p -> !p.isEmpty()).toList().size() == 1) {
                ItemStack pint = itemStackHandler.extractItem(0, 1, false);
                pPlayer.setItemInHand(pHand, pint);
            } else {

                float x = (float) (pHit.getLocation().x - pPos.getX());
                float z = (float) (pHit.getLocation().z - pPos.getZ());
                float threshold = 0.5f;

                int slot = (blockFacing == Direction.NORTH && x >= 0.5) ||
                        (blockFacing == Direction.SOUTH && x < 0.5) ||
                        (blockFacing == Direction.EAST && z >= 0.5) ||
                        (blockFacing == Direction.WEST && z < 0.5) ? 0 : 1;

                ItemStack pint = itemStackHandler.extractItem(slot, 1, false);
                pPlayer.setItemInHand(pHand, pint);
            }

        }
        checkAndRemoveIfInvalid();
        syncBlockEntity();
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public void checkAndRemoveIfInvalid() {
        int count = 0;

        for (ItemStack beer : getBeers()) {
            if (!beer.isEmpty()) {
                count++;
            }
        }

        if (count == 1) {
            ItemStack br = ItemStack.EMPTY;
            for (ItemStack beer : getBeers()) {
                if (!beer.isEmpty()) br = beer;
            }
            this.itemStackHandler.setStackInSlot(1, ItemStack.EMPTY);
            this.itemStackHandler.setStackInSlot(0, br);
        }

        if (count == 0) {
            level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
        }
    }

    //        float z = (float) (pHit.getLocation().z - pPos.getZ());
//        Direction blockFacing  =getBlockState().getValue(HorizontalDirectionalBlock.FACING);
//        boolean otherBeer = z < 0.8;
//
//        if (pHit.getDirection() == Direction.NORTH) {
//            otherBeer = z < 0.5;
//        }
//
//        int count = 0;
//
//        for (ItemStack beer : getBeers()) {
//            if (!beer.isEmpty()) {
//                count++;
//            }
//        }
//
//        if (count == 1) {
//            if (pPlayer.getMainHandItem().isEmpty()) {
//
//                for (int i = 0; i < getBeers().size(); i++) {
//                    ItemStack stack = getBeers().get(i);
//                    if (!stack.isEmpty()) {
//                        pPlayer.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
//                        stack.shrink(1);
//                    }
//                }
//            }
//        }
//
//        int a1 = blockFacing == Direction.NORTH ? 1 : 0;
//        int b1 = blockFacing == Direction.NORTH ? 0 : 1;
//
//        if (otherBeer) {
//            IFluidHandlerItem item = getBeers().get(a1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
//            if (item instanceof FluidInventoryItem inventoryItem) {
//                pPlayer.sendSystemMessage(Component.translatable(inventoryItem.getFluid().getTranslationKey()));
//                if (pPlayer.getMainHandItem().isEmpty()) {
//                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, getBeers().get(a1).copy());
//                    getBeers().set(a1, ItemStack.EMPTY);
//                }
//            }
//        } else {
//            IFluidHandlerItem item = getBeers().get(b1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
//            if (item instanceof FluidInventoryItem inventoryItem) {
//                pPlayer.sendSystemMessage(Component.translatable(inventoryItem.getFluid().getTranslationKey()));
//                if (pPlayer.getMainHandItem().isEmpty()) {
//                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, getBeers().get(b1).copy());
//                    getBeers().set(b1, ItemStack.EMPTY);
//                }
//            }
//        }
//        checkAndRemoveIfInvalid();

}
