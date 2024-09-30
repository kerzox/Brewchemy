package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.common.blockentity.base.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestDataPacket {

    private BlockPos positionWantingUpdate;

    public static void get(BlockPos pos) {
        PacketHandler.sendToServer(new RequestDataPacket(pos));
    }

    public RequestDataPacket(BlockPos pos) {
        this.positionWantingUpdate = pos;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.positionWantingUpdate);
    }

    public RequestDataPacket(FriendlyByteBuf buf) {
        this.positionWantingUpdate = buf.readBlockPos();
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
            BlockEntity be = player.level().getBlockEntity(packet.positionWantingUpdate);
            if (be instanceof SyncedBlockEntity syncedBlockEntity) {
                syncedBlockEntity.syncBlockEntity();
            }
        }
    }
}
