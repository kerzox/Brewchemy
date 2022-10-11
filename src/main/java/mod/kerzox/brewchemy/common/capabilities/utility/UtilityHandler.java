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

import java.util.*;

public class UtilityHandler implements IUtilityItem, ICapabilityProvider {

    private List<Capability<?>> capabilities = new ArrayList<>();

    private Capability<?> current = ForgeCapabilities.FLUID_HANDLER;
    private Capability<?> locked;

    private final LazyOptional<IUtilityItem> holder = LazyOptional.of(() -> this);

    public UtilityHandler(Capability<?> lockTo) {
        this.locked = lockTo;
    }

    public UtilityHandler(Capability<?>... capabilities) {
        addCapabilities(capabilities);
    }

    public static UtilityHandler of(Capability<?>... capabilities) {
        return new UtilityHandler(capabilities);
    }

    public void addCapabilities(Capability<?>... capabilities) {
        this.capabilities.addAll(Arrays.asList(capabilities));
    }

    @Override
    public Capability<?> cycleModes(boolean reverse) {
        int index = capabilities.indexOf(current);

        if (reverse) index = index - 1;
        else index = index + 1;

        if (index >= capabilities.size()) index = 0;
        else if (index < 0) index = capabilities.size() - 1;

        current = capabilities.get(index);
        return current;
    }

    public Capability<?> getCurrent() {
        return current;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == BrewchemyCapabilities.UTILITY_CAPABILITY ? holder.cast() : LazyOptional.empty();
    }
}
