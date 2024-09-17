package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * When a menu screen is opened this will keep the client in sync with the server
 */

public class SyncContainer {

    public static void handle() {
       PacketHandler.sendToServer(new SyncContainer());
    }

    public SyncContainer() {

    }

    public SyncContainer(FriendlyByteBuf buf) {

    }

    public static boolean handle(SyncContainer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(SyncContainer packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            if (player.containerMenu instanceof DefaultMenu<?> menu) {
                menu.getBlockEntity().syncBlockEntity();
            }
        }
    }
}
