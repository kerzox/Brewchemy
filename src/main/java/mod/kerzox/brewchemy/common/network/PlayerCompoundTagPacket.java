package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import mod.kerzox.brewchemy.common.capabilities.drunk.IntoxicationManager;
import mod.kerzox.brewchemy.common.network.client.CompoundTagPacketClient;
import mod.kerzox.brewchemy.common.network.client.PlayerCompoundTagPacketClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PlayerCompoundTagPacket {

    String type;
    CompoundTag tag;

    public PlayerCompoundTagPacket(String str, CompoundTag up) {
        this.tag = up;
        this.type = str;
    }

    public PlayerCompoundTagPacket(FriendlyByteBuf buf) {
        this.tag = buf.readAnySizeNbt();
        this.type = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeUtf(type);
    }

    public CompoundTag getTag() {
        return tag;
    }

    public String getType() {
        return type;
    }

    public static boolean handle(PlayerCompoundTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
            else DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PlayerCompoundTagPacketClient.handleOnClient(packet, ctx));
        });
        return true;
    }

    private static void handleOnServer(PlayerCompoundTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            if (Objects.equals(IntoxicationManager.INTOXICATION_CAPABILITY.getName(), packet.type)) {
                player.getCapability(IntoxicationManager.INTOXICATION_CAPABILITY).ifPresent(intoxicationManager -> intoxicationManager.deserializeNBT(packet.tag));
            }
        }
    }
}
