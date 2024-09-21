package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import mod.kerzox.brewchemy.common.network.client.CompoundTagPacketClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompoundTagPacket {

    CompoundTag tag;

    public CompoundTagPacket(CompoundTag up) {
        this.tag = up;
    }

    public CompoundTagPacket(String str) {
        CompoundTag tag = new CompoundTag();
        tag.putString(str, str);
        this.tag = tag;
    }

    public CompoundTagPacket(String str, int value) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(str, value);
        this.tag = tag;
    }

    public CompoundTagPacket(String str, float value) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(str, value);
        this.tag = tag;
    }

    public CompoundTagPacket(String str, double value) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble(str, value);
        this.tag = tag;
    }

    public CompoundTagPacket(FriendlyByteBuf buf) {
        this.tag = buf.readAnySizeNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public static boolean handle(CompoundTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
            else DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CompoundTagPacketClient.handleOnClient(packet.tag, ctx));
        });
        return true;
    }

    private static void handleOnServer(CompoundTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {
                menu.getBlockEntity().updateFromNetwork(packet.tag);
            }
        }
    }

}
