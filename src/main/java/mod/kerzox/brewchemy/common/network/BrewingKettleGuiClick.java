package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.ui.menu.BrewingMenu;
import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidInventory;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackHandlerUtils;
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


public class BrewingKettleGuiClick {

    private ItemStack itemStack;
    private int button;

    public BrewingKettleGuiClick(ItemStack itemInHand, int button) {
        this.itemStack = itemInHand;
        this.button = button;
    }

    public BrewingKettleGuiClick(FriendlyByteBuf buf) {
        this.itemStack = buf.readItem();
        this.button = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(this.itemStack);
        buf.writeInt(this.button);
    }

    public static boolean handle(BrewingKettleGuiClick packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(BrewingKettleGuiClick packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof BrewingMenu brewingMenu) {
                Optional<IFluidHandlerItem> optionalIFluidHandlerItem = packet.itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
                if (optionalIFluidHandlerItem.isPresent()) {
                    if (brewingMenu.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get() instanceof MultifluidInventory inventory) {
                        IFluidHandlerItem iFluidHandlerItem = optionalIFluidHandlerItem.get();
                        for (int i = 0; i < iFluidHandlerItem.getTanks(); i++) {
                            FluidStack stack1 = iFluidHandlerItem.getFluidInTank(i);
//                            if (packet.button == 1) {
//                                tryToFillContainer(packet, menu, stack, iFluidHandlerItem, i, player);
//                            } else {
//
//                                IFluidHandler temp = MultifluidTank.of(1, inventory.getTankCapacity(i));
//                                temp.fill(inventory.getFluidInTank(i), IFluidHandler.FluidAction.EXECUTE);
//
//                                FluidActionResult result =
//                                        FluidUtil.tryEmptyContainer(
//                                                packet.itemStack,
//                                                ,
//                                                iFluidHandlerItem.getTankCapacity(i), player, true);
//                                if (result.success) {
//                                    menu.setCarried(result.result);
//                                }
                            }
                        }
                    }
                else {
                    if (packet.button == 0) {
                        brewingMenu.getBlockEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(item -> {
                            ItemStackHandlerUtils.insertAndModifyStack(item, packet.itemStack);
                        });
                        brewingMenu.setCarried(packet.itemStack);
                    } else if (packet.button == 1) {

                        ItemStack carried = packet.itemStack;

                        brewingMenu.getBlockEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                            for (int i = 0; i < handler.getSlots(); i++) {
                                ItemStack insideKettle = handler.getStackInSlot(i);
                                if (!insideKettle.isEmpty()) {
                                    if (carried.isEmpty()) {
                                        brewingMenu.setCarried(handler.extractItem(i, player.isShiftKeyDown() ? insideKettle.getCount() : 1, false));
                                        return;
                                    } else if (carried.getCount() < carried.getMaxStackSize()) {
                                        int spaceLeft = carried.getMaxStackSize() - carried.getCount();
                                        int amountToExtract;
                                        if (player.isShiftKeyDown()) {
                                            amountToExtract = Math.min(insideKettle.getCount(), spaceLeft);
                                        } else {
                                            amountToExtract = 1;
                                        }
                                        ItemStack extracted = handler.extractItem(i, amountToExtract, false);
                                        if (!extracted.isEmpty()) {
                                            carried.grow(extracted.getCount());
                                        }
                                        brewingMenu.setCarried(carried);
                                    }
                                }
                            }
                        });

                    }
                }

            }
        }
    }

}
