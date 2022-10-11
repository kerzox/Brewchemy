package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.gui.menu.DefaultMenu;
import mod.kerzox.brewchemy.common.capabilities.utility.UtilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static mod.kerzox.brewchemy.common.capabilities.BrewchemyCapabilities.UTILITY_CAPABILITY;

public class UtilityModeCycle {

    boolean up;

    public UtilityModeCycle(boolean up) {
        this.up = up;
    }

    public UtilityModeCycle(FriendlyByteBuf buf) {
        this.up = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(up);
    }

    public static boolean handle(UtilityModeCycle packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(UtilityModeCycle packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            player.getMainHandItem().getCapability(UTILITY_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof UtilityHandler utilityHandler) {
                    player.sendSystemMessage(Component.literal(utilityHandler.cycleModes(packet.up).getName()));
                }
            });
        }
    }

}
