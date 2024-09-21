package mod.kerzox.brewchemy.client.ui.menu;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import mod.kerzox.brewchemy.common.blockentity.BrewingKettleBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.MillingBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BrewingMenu extends DefaultMenu<BrewingKettleBlockEntity> {

    public BrewingMenu(int pContainerId, Inventory playerInventory, Player player, BrewingKettleBlockEntity blockEntity) {
        super(BrewchemyRegistry.Menus.BREWING_MENU.get(), pContainerId, playerInventory, player, blockEntity);
        layoutPlayerInventorySlots(8, 83);


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
