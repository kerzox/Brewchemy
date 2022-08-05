package mod.kerzox.brewchemy.client.gui.menu;

import mod.kerzox.brewchemy.common.blockentity.GerminationChamberBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.WoodenBarrelBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class FermentationBarrelMenu extends DefaultMenu<WoodenBarrelBlockEntity>{

    public FermentationBarrelMenu(int pContainerId, Inventory playerInventory, Player player, WoodenBarrelBlockEntity blockEntity) {
        super(BrewchemyRegistry.Menus.FERMENTATION_BARREL_MENU.get(), pContainerId, playerInventory, player, blockEntity);
        layoutPlayerInventorySlots(8, 84);
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            addSlot(cap, 0, 40, 52);
            addSlot(cap, 1, 112, 20);
            addSlot(cap, 2, 112, 52);
        });
    }

    @Override
    protected ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index) {
        if (copied.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
            if (!this.moveItemStackTo(copied, 37, 38, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if (!this.moveItemStackTo(copied, 36, 39, false)) {
            return ItemStack.EMPTY;
        }
        return copied;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
