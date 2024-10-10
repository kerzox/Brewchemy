package mod.kerzox.brewchemy.common.network.client;

import mod.kerzox.brewchemy.common.capabilities.drunk.IntoxicationManager;
import mod.kerzox.brewchemy.common.network.PlayerCompoundTagPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PlayerCompoundTagPacketClient {

    public static void handleOnClient(PlayerCompoundTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (Objects.equals(IntoxicationManager.INTOXICATION_CAPABILITY.getName(), packet.getType())) {
            Minecraft.getInstance().player.getCapability(IntoxicationManager.INTOXICATION_CAPABILITY).ifPresent(intoxicationManager -> intoxicationManager.deserializeNBT(packet.getTag()));
        }

    }

}
