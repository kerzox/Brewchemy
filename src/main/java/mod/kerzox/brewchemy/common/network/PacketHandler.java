package mod.kerzox.brewchemy.common.network;

import mod.kerzox.brewchemy.Brewchemy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Brewchemy.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextID() {
        return ID++;
    }

    public static void register() {
        INSTANCE.messageBuilder(SyncContainer.class, nextID())
                .encoder(SyncContainer::toBytes)
                .decoder(SyncContainer::new)
                .consumerMainThread(SyncContainer::handle)
                .add();
        INSTANCE.messageBuilder(FluidTankClick.class, nextID())
                .encoder(FluidTankClick::toBytes)
                .decoder(FluidTankClick::new)
                .consumerMainThread(FluidTankClick::handle)
                .add();
        INSTANCE.messageBuilder(BrewingKettleGuiClick.class, nextID())
                .encoder(BrewingKettleGuiClick::toBytes)
                .decoder(BrewingKettleGuiClick::new)
                .consumerMainThread(BrewingKettleGuiClick::handle)
                .add();
        INSTANCE.messageBuilder(CompoundTagPacket.class, nextID())
                .encoder(CompoundTagPacket::toBytes)
                .decoder(CompoundTagPacket::new)
                .consumerMainThread(CompoundTagPacket::handle)
                .add();
    }

    public static void sendToClientPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToAllClientsFromChunk(Object packet, LevelChunk chunk) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

}
