package mod.kerzox.brewchemy.common.capabilities.drunk;

import mod.kerzox.brewchemy.common.fluid.alcohol.AgeableAlcoholStack;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.common.network.PlayerCompoundTagPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

@AutoRegisterCapability
public class IntoxicationManager implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<IntoxicationManager> INTOXICATION_CAPABILITY = get(new CapabilityToken<>(){});
    private LazyOptional<IntoxicationManager> handler = LazyOptional.of(() -> this);
    private Player player;

    private double intoxicationAmount = 0;

    public IntoxicationManager(Player player) {
        this.player = player;
    }

    public static double calculateContentFromFluid(int tankCapacity, AgeableAlcoholStack alcoholStack) {
        int ticksFermented = alcoholStack.getAge();
        int optimalStart = alcoholStack.getPerfectionRange()[0];
        int optimalEnd = alcoholStack.getPerfectionRange()[1];
        double bonusFactor = 0.1; // 10% bonus for fermentation in optimal range
        double penaltyFactor = 0.05; // 5% penalty for over-fermentation

        double alcoholTicks = 0;

        if (alcoholStack.getAge() >= alcoholStack.getMaturationStart()) {
            alcoholTicks = (int) (ticksFermented * 0.001f);
            alcoholTicks = Math.max(1, alcoholTicks);
        }

        if (alcoholStack.inPerfectionRange()) {
            int timeInsidePerfection = (Math.min(ticksFermented, optimalEnd) - optimalStart) + 1;
            alcoholTicks *= (int) (timeInsidePerfection * (1 + bonusFactor));
        }

        if (alcoholStack.overFermented()) {
            int over = (int) (Math.max(1, ticksFermented - alcoholStack.getSpoiledStart()) * penaltyFactor);
            alcoholTicks /= over;
        }

        return alcoholTicks / alcoholStack.getAmount();
    }

    public static void applyAlcoholToPlayer(Player player, double content) {
        player.getCapability(INTOXICATION_CAPABILITY).ifPresent(handler -> {
            handler.increaseIntoxication(content);
            PacketHandler.sendToClientPlayer(new PlayerCompoundTagPacket(INTOXICATION_CAPABILITY.getName(), handler.serializeNBT()), (ServerPlayer) player);
        });

    }

    public void increaseIntoxication(double amount) {
        this.intoxicationAmount += amount;
    }

    public void decreaseIntoxication(double amount) {
        this.intoxicationAmount -= amount;
    }

    public void setIntoxication(double amount) {
        this.intoxicationAmount = amount;
    }

    public void detox() {
        this.intoxicationAmount = 0;
    }

    public void tick() {

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return INTOXICATION_CAPABILITY.orEmpty(cap, handler);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("intoxicationAmount", intoxicationAmount);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.intoxicationAmount = nbt.getDouble("intoxicationAmount");
    }
}
