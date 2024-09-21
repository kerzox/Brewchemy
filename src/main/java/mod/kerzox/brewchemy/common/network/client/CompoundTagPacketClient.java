package mod.kerzox.brewchemy.common.network.client;

import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import mod.kerzox.brewchemy.common.network.CompoundTagPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompoundTagPacketClient {

    public static void handleOnClient(CompoundTag packet, Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().player.containerMenu instanceof DefaultMenu<?> menu) {
            CompoundTag tag = new CompoundTag();
            tag.put("client_update", packet);
            menu.getBlockEntity().updateFromNetwork(tag);
        }

    }

}
