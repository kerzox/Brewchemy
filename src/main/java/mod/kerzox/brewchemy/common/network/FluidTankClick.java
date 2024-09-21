package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidInventory;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidTank;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class FluidTankClick {

    private int index;
    private int button;

    public FluidTankClick(int index, int button) {
        this.index = index;
        this.button = button;
    }

    public FluidTankClick(FriendlyByteBuf buf) {
        this.index = buf.readInt();
        this.button = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(index);
        buf.writeInt(button);
    }

    public static boolean handle(FluidTankClick packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(FluidTankClick packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {

                ItemStack stack = menu.getCarried();
                Optional<IFluidHandlerItem> optionalIFluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();

                if (optionalIFluidHandlerItem.isPresent()) {

                    IFluidHandlerItem iFluidHandlerItem = optionalIFluidHandlerItem.get();

                    for (int i = 0; i < iFluidHandlerItem.getTanks(); i++) {

                        FluidStack stack1 = iFluidHandlerItem.getFluidInTank(i);

                        if (packet.button == 1) {
                            tryToFillContainer(packet, menu, stack, iFluidHandlerItem, i, player);
                        } else {
                            // try to insert

                            if (menu.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get() instanceof MultifluidInventory inventory) {
                                FluidActionResult result = FluidUtil.tryEmptyContainer(stack, inventory.getInputWrapper(), iFluidHandlerItem.getTankCapacity(i), player, true);
                                if (result.success) {
                                    menu.setCarried(result.result);
                                    continue;
                                }
                            }
                        }


                    }



                }
            }
        }
    }

    private static boolean tryToFillContainer(FluidTankClick packet, DefaultMenu<?> menu, ItemStack stack, IFluidHandlerItem iFluidHandlerItem, int i, Player player) {

        if (menu.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get() instanceof MultifluidInventory inventory) {
            // create a temp handler
            IFluidHandler temp = MultifluidTank.of(1, inventory.getTankCapacity(packet.index));
            temp.fill(inventory.getFluidInTank(packet.index), IFluidHandler.FluidAction.EXECUTE);

            FluidActionResult result = FluidUtil.tryFillContainer(stack, temp, iFluidHandlerItem.getTankCapacity(i), player, true);
            if (result.success) {
                menu.setCarried(result.result);
                inventory.getFluidInTank(packet.index).shrink(inventory.getFluidInTank(packet.index).getAmount() - temp.getFluidInTank(0).getAmount());
                return true;
            }
        }
        return false;
    }

}
