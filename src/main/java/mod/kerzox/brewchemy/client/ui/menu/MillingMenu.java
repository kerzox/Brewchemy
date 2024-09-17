package mod.kerzox.brewchemy.client.ui.menu;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import mod.kerzox.brewchemy.common.blockentity.MillingBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

public class MillingMenu extends DefaultMenu<MillingBlockEntity> {

    public MillingMenu(int pContainerId, Inventory playerInventory, Player player, MillingBlockEntity blockEntity) {
        super(BrewchemyRegistry.Menus.MILLING_MENU.get(), pContainerId, playerInventory, player, blockEntity);
        layoutPlayerInventorySlots(8, 84);
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 44, 32);
            addSlot(cap, 1, 116, 32);
        });

    }

    @Override
    protected ItemStack trySlotShiftClick(Player player, ItemStack copied, ItemStack realStack, int index) {
        return copied;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
