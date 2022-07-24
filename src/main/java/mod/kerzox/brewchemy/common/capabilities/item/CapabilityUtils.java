package mod.kerzox.brewchemy.common.capabilities.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CapabilityUtils {

    public static boolean tryPlayerInventoryInsert(IItemHandlerModifiable handler, int slot, Player player) {
        ItemStack sim = handler.extractItem(0, handler.getStackInSlot(slot).getCount(), true);
        if (sim.isEmpty()) return false;
        if (player.addItem(sim)) return player.addItem(handler.extractItem(0, handler.getStackInSlot(slot).getCount(), false));
        else return false;
    }

}
