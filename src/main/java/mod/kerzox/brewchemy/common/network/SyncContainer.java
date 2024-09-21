package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * When a menu screen is opened this will keep the client in sync with the server
 */

public class SyncContainer {

    int state;

    public static void handle() {
       PacketHandler.sendToServer(new SyncContainer(1));
    }

    public static void onOpen()  {
        PacketHandler.sendToServer(new SyncContainer(0));
    }

    public SyncContainer(int state) {
        this.state = state;
    }

    public SyncContainer(FriendlyByteBuf buf) {
       this.state = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(state);
    }

    public static boolean handle(SyncContainer packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(SyncContainer packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            if (player.containerMenu instanceof DefaultMenu<?> menu) {
                menu.getBlockEntity().syncBlockEntity();
                menu.getBlockEntity().onMenuSync(player, packet.state);
            }
        }
    }


}
