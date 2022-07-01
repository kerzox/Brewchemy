package mod.kerzox.brewchemy.client.gui.menu;

import mod.kerzox.brewchemy.common.blockentity.GerminationChamberBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.WoodenBarrelBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class FermentationBarrelMenu extends DefaultMenu<WoodenBarrelBlockEntity>{

    public FermentationBarrelMenu(int pContainerId, Inventory playerInventory, Player player, WoodenBarrelBlockEntity blockEntity) {
        super(BrewchemyRegistry.Menus.FERMENTATION_BARREL_MENU.get(), pContainerId, playerInventory, player, blockEntity);
        layoutPlayerInventorySlots(8, 84);
    }

    @Override
    protected ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index) {
        return copied;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
