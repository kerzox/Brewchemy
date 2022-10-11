package mod.kerzox.brewchemy.client.gui.menu;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.blockentity.MillStoneBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

public class MillstoneMenu extends DefaultMenu<MillStoneBlockEntity>{

    public MillstoneMenu(int pContainerId, Inventory playerInventory, Player player, MillStoneBlockEntity blockEntity) {
        super(BrewchemyRegistry.Menus.MILLSTONE_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        layoutPlayerInventorySlots(8, 84);
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            addSlot(cap, 0, 44, 32);
            addSlot(cap, 1, 116, 32);
        });
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    protected ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index) {
        if (!this.moveItemStackTo(copied, 36, 37, false)) {
            return ItemStack.EMPTY;
        }
        return copied;
    }
}
