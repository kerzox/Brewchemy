package mod.kerzox.brewchemy.common.capabilities;

import mod.kerzox.brewchemy.common.capabilities.fluid.ItemStackTankFluidCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CapabilityUtils {

    public static boolean tryPlayerInventoryInsert(IItemHandlerModifiable handler, int slot, Player player) {
        ItemStack sim = handler.extractItem(0, handler.getStackInSlot(slot).getCount(), true);
        if (sim.isEmpty()) return false;
        if (player.addItem(sim)) return player.addItem(handler.extractItem(0, handler.getStackInSlot(slot).getCount(), false));
        else return false;
    }

    public static ItemStackTankFluidCapability getTankFromItemStack(ItemStack stack) {
        if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseGet(null) instanceof ItemStackTankFluidCapability capability) return capability;
        else return null;
    }

}
