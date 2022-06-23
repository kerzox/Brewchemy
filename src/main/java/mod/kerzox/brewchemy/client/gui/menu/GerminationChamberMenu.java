package mod.kerzox.brewchemy.client.gui.menu;

import mod.kerzox.brewchemy.common.blockentity.GerminationChamberBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.MillStoneBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class GerminationChamberMenu extends DefaultMenu<GerminationChamberBlockEntity>{

    public GerminationChamberMenu(int pContainerId, Inventory playerInventory, Player player, GerminationChamberBlockEntity blockEntity) {
        super(BrewchemyRegistry.Menus.GERMINATION_CHAMBER_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        layoutPlayerInventorySlots(8, 84);
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            addSlotRange(cap, 0, 36, 16, 6, 18);
            addSlot(cap, 6, 81, 51);
        });
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
