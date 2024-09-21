package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestDataPacket {

    public static void get() {
        PacketHandler.sendToServer(new RequestDataPacket());
    }

    public RequestDataPacket() {

    }

    public RequestDataPacket(FriendlyByteBuf buf) {

    }

    public static boolean handle(RequestDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(RequestDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            if (player.containerMenu instanceof DefaultMenu<?> menu) {

            }
        }
    }
}
