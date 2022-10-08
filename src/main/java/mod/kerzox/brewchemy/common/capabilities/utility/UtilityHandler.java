package mod.kerzox.brewchemy.common.capabilities.utility;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.capabilities.BrewchemyCapabilities;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UtilityHandler implements IUtilityItem, ICapabilityProvider {

    private Capability<?> current = ForgeCapabilities.FLUID_HANDLER;
    private Capability<?> locked;

    private int used;
    private int maxAmount = 3;

    private final LazyOptional<IUtilityItem> holder = LazyOptional.of(() -> this);

    public UtilityHandler(Capability<?> lockTo) {
        this.locked = lockTo;
    }

    public UtilityHandler() {

    }

    public Capability<?> getCurrent() {
        return current;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == BrewchemyCapabilities.UTILITY_CAPABILITY ? holder.cast() : LazyOptional.empty();
    }

    @Override
    public int getUseAmount() {
        return used;
    }

    @Override
    public void add() {
        if (this.used >= maxAmount) {
            this.used = 0;
            return;
        };
        this.used++;
    }

    @Override
    public void subtract() {
        if (this.used <= 0) return;
        this.used--;
    }

    @Override
    public void set(int amount) {
        this.used = amount;
    }
}
